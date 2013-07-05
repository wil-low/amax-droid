#!/usr/bin/perl
use strict;
use warnings;

if (!defined($ARGV[1])) {
	die "Usage: generate_keys.pl <start year> <final year>";
}

my $START_YEAR = $ARGV[0];
my $FINAL_YEAR = $ARGV[1];

for (my $year = $START_YEAR; $year <= $FINAL_YEAR; ++$year) {
	make_year ($year, 1);
	make_year ($year, 2);
	make_year ($year, 3);
	make_year ($year, 6);
	make_year ($year, 12);
}

sub make_year {  # year, month_count
	my ($year, $month_count) = @_;
	for (my $month = 0; $month < 12; $month += $month_count) {
		my $key = `< /dev/urandom tr -dc a-z0-9 | head -c16`;
		print "$year\t$month\t$month_count\t$key\n";
	}
}
