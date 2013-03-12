#!/usr/bin/perl
use strict;
use warnings;

if (!defined($ARGV[2])) {
	die "Usage: calculate_demo.pl <year> <month> <month_count>";
}

$ENV{CALCULATIONS_DIR} = '/home/willow/prj/amax/amax-calculations';

my $DEST_DIR = '/home/willow/prj/amax-droid-hg/Astromaximum/assets';

my ($YEAR, $MONTH, $MONTH_COUNT) = @ARGV;
my $key = find_key($YEAR, $MONTH, $MONTH_COUNT);

make_common ();
make_location();


sub make_common {
	my $common_file = "$DEST_DIR/common.dat";
	my $cmd = "java -jar Mutter3.jar common $YEAR $MONTH $MONTH_COUNT $common_file";
	system ($cmd);
}


sub make_location {   # $country, $city_id
	my $location_file = "$DEST_DIR/locations.dat";
	my $cmd = "java -jar Mutter3.jar locations $YEAR $MONTH $MONTH_COUNT data/demo_loclist.txt $location_file";
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
