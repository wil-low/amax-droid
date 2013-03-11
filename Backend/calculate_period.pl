#!/usr/bin/perl
use strict;
use warnings;
use IO::Compress::Gzip qw(gzip $GzipError);
use File::Find;
use File::Path;
use Cwd;

if (!defined($ARGV[2])) {
	die "Usage: calculate_period.pl <year> <month> <month_count>";
}
my $SCRIPT_DIR = getcwd();
$ENV{CALCULATIONS_DIR} = '/home/willow/prj/amax/amax-calculations';

my ($YEAR, $MONTH, $MONTH_COUNT) = @ARGV;
my $key = find_key($YEAR, $MONTH, $MONTH_COUNT);

my $PERIOD = sprintf ('%04d_%02d_%02d', $YEAR, $MONTH, $MONTH_COUNT);
my $DEST_DIR = "data/$PERIOD";
mkdir ($DEST_DIR);

my $DEPLOY_TMP_DIR = "deploy_tmp/$key";
File::Path::remove_tree ('deploy_tmp');
File::Path::make_path ($DEPLOY_TMP_DIR);

make_common ();

find ({wanted => \&location_found, no_chdir => 1}, $ENV{CALCULATIONS_DIR} . "/archive/$YEAR");

compress_year();

sub make_common {
	my $common_file = "$DEST_DIR/common.dat";
	if (! -f $common_file) {
		my $cmd = "java -jar Mutter3.jar common $YEAR $MONTH $MONTH_COUNT $common_file";
		system ($cmd);
	}
}


sub make_location {   # $country, $city_id
	my ($country, $city_id) = @_;
	my $location_file = "$DEST_DIR/$city_id.dat";
	if (! -f $location_file) {
		my $cmd = "java -jar Mutter3.jar location $YEAR $MONTH $MONTH_COUNT $country $city_id $location_file";
		system ($cmd);
	}
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


sub location_found {
	if ($File::Find::name =~ /.+\/(.+?)\/(\w{8})\.dat$/) {
		my ($country, $city_id) = ($1, $2);
		make_location ($country, $city_id);
	}
}

sub compress_year {
	find ({wanted => \&datafile_found, no_chdir => 1}, $DEST_DIR);
	mkdir ('deploy');
	my $tar_file = "$SCRIPT_DIR/deploy/$PERIOD.tgz";
	unlink ($tar_file);
	my $cmd = "tar czf $tar_file -C deploy_tmp $key";
	#die $cmd;
	system ($cmd);
}

sub datafile_found {
	if ($File::Find::name =~ /(\w+?)\.dat$/) {
		my $city_id = $1;
		if ($city_id eq 'common') {
			my $OUTF;
			open ($OUTF, ">$DEPLOY_TMP_DIR/common");
			print ($OUTF $key);
			gzip $File::Find::name => $OUTF, Minimal => 1, Append => 1 or die "gzip failed: $GzipError\n";
			close ($OUTF);
		}
		else {
			gzip $File::Find::name => "$DEPLOY_TMP_DIR/$city_id", Minimal => 1 or die "gzip failed: $GzipError\n";
		}
	}
}
