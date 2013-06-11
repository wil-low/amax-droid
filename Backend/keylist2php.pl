#!/usr/bin/perl
use strict;
use warnings;

if (!defined ($ARGV[0])) {
	die "Usage: keylist2php.pl <key list file>";
}

open (INF, "<$ARGV[0]") or die "$!: $ARGV[0]";

print << 'EOF';
<?php
	$GLOBALS['amax_droid_keys'] = array (
EOF

while (my $line = <INF>) {
	chomp ($line);
	my ($year, $month, $month_count, $key) = split (/\t/, $line);
	print (sprintf ("\t\t'%04d%02d%02d' => '%s',\n", $year, $month, $month_count, $key));
}

close (INF);

print << 'EOF';
	);
?>
EOF
