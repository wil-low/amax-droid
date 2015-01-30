#!/usr/bin/perl
use strict;
use warnings;

if (!defined($ARGV[2])) {
	die "Usage: calculate_demo.pl <year> <month 0-11> <month_count>";
}

$ENV{CALCULATIONS_DIR} = '/home/willow/prj/amax/amax-calculations';

my $DEST_DIR = '/home/willow/prj/amax-droid-hg/Astromaximum/app/assets';
my $LOCLIST = 'data/demo_loclist.txt';
my $LOCLIST_CSV = '/tmp/demo_loclist.csv';
my $TMP_SQL = '/tmp/amax.sql';
my $TMP_DATABASE = '/tmp/amax.db';
my $DB_PATH = "$DEST_DIR/databases";
my $ZIPPED_DB = "$DB_PATH/amax.zip";

my ($YEAR, $MONTH, $MONTH_COUNT) = @ARGV;
my $key = find_key($YEAR, $MONTH, $MONTH_COUNT);

make_common ();
make_location();
make_database();


sub make_common {
	my $common_file = "$DEST_DIR/common.dat";
	my $cmd = "java -jar Mutter3.jar common $YEAR $MONTH $MONTH_COUNT $common_file";
	system ($cmd);
}


sub make_location {   # $country, $city_id
	my $location_file = "$DEST_DIR/locations.dat";
	my $cmd = "java -jar Mutter3.jar locations $YEAR $MONTH $MONTH_COUNT $LOCLIST $location_file $LOCLIST_CSV";
	system ($cmd);
}


sub find_key {
	my $key;
	my $needle = "$YEAR\t$MONTH\t$MONTH_COUNT";

	my $KEYFILE = 'data/year_keys.txt';
	open (KEYFILE, "<$KEYFILE") or die "$!: $KEYFILE";
	while (my $line = <KEYFILE>) {
		if ($line =~ /^$needle\t(\w{16})/) {
			$key = $1;
			last;
		}
	}
	close(KEYFILE);

	if (!defined ($key)) {
		die "Key not found for $needle\n";
	}

	return $key;
}

sub make_database {
	open (LOCLIST_CSV, "<$LOCLIST_CSV") or die "$!: $LOCLIST_CSV";
	open (TMP_SQL, ">$TMP_SQL") or die "$!: $TMP_SQL";
	print (TMP_SQL "BEGIN TRANSACTION;\n");
	my $sql = <<END;
	CREATE TABLE commons (_id INTEGER PRIMARY KEY, year NUMERIC, start_month NUMERIC, month_count NUMERIC, key TEXT UNIQUE);
	CREATE TABLE cities (_id INTEGER PRIMARY KEY, name TEXT, state TEXT, country TEXT, key TEXT UNIQUE);
	CREATE TABLE locations (_id INTEGER PRIMARY KEY, common_id NUMERIC, city_id NUMERIC);

END
	print (TMP_SQL $sql);
	print (TMP_SQL "\tINSERT INTO commons (_id, year, start_month, month_count, key) VALUES (1, $YEAR, $MONTH, $MONTH_COUNT, '$key');\n");
	my $counter = 1;
	while (my $line = <LOCLIST_CSV>) {
		chomp($line);
		$line =~ s/'/''/g;
		my ($name, $state, $country, $timezone, $city_key) = split (/;/, $line);
		print (TMP_SQL "\tINSERT INTO cities (_id, name, state, country, key) VALUES ($counter, '$name', '$state', '$country', '$city_key');\n");
		print (TMP_SQL "\tINSERT INTO locations (_id, common_id, city_id) VALUES ($counter, 1, $counter);\n");
		++$counter;
	}
	print (TMP_SQL "COMMIT;\n");
	close (TMP_SQL);
	close(LOCLIST_CSV);
	unlink ($TMP_DATABASE);
	my $cmd = "sqlite3 $TMP_DATABASE < $TMP_SQL";
	system ($cmd);
	mkdir ($DB_PATH);
	$cmd = "zip --junk-paths $ZIPPED_DB $TMP_DATABASE";
	system ($cmd);
}
