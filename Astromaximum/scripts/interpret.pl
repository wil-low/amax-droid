#!/usr/bin/perl
use strict;
use warnings;

my $AMAX_PATH = '/home/willow/prj/amax/amax-hg/Astromaximum';

require "$AMAX_PATH/tools.pm";

my $lang = $ARGV[0];

my @interpret_files = glob("$AMAX_PATH/Astromaximum/interpret/$lang/*.txt");

my $out = << "END";
<?xml version="1.0" encoding="utf-8"?>
<resources>
END

foreach my $infile (@interpret_files) {
	open (INF, "<$infile") or die "$!: $infile";
	my @buf = <INF>;
	close (INF);
	$buf[0] =~ /\!\!type\s*(\w+)/i;
	my $evt=$1;
	$evt =~ s/EV_DEGPASS\d+/EV_DEGREE_PASS/;
	my $event_type = $tools::eventType{$evt};
	if ($event_type !~ /^\d+$/) {
		die "Event $evt not defined in $infile! Skipped";
		next;
	}
	if ($evt eq 'EV_MSG') {
		next;
	}
	$buf[2]=~/\!\!planet\s*(.+?)[\n\r]+/i;
	my $planet=$1;
	if ($planet == -1) {
		$planet = '';
	}
	foreach my $line (@buf) {
		$line =~ s/\/\/.+//is;
		next if $line !~ /%[\d\s\,\-]+%/;
		$line =~ s/\s*\Z//is;
		$line =~ s/\.+\Z//is if $evt ne 'EV_MSG';
		$line =~ s/.*?%(.*?)%\s*//is;
		my @param = split (/,/, $1);
		for (my $i = 0; $i < 3; ++$i) {
			if (!defined ($param[$i])) {
				$param[$i] = '';
			}
			else {
				$param[$i] = int($param[$i]);
			}
		}
		$line =~ s/"/\\"/g;
		$line =~ s/'/\\'/g;
		$line =~ s/[\*\~\#\^\$\}\>\@\=\{]//g;
		$line =~ s/\|/<\/p><p>/g;
		$line =~ s/<p>--<\/p>/<hr\/>/g;
		$line =~ s/--/&#8212;/g;
		$line =~ s/&/&amp;/g;
		$line =~ s/</&lt;/g;
		$line =~ s/>/&gt;/g;
		
		$out .= << "END";
		<string name="int$planet\_$event_type\_$param[0]\_$param[1]\_$param[2]">$line</string>
END
	}
}
$out .= "</resources>\n";
print $out;

