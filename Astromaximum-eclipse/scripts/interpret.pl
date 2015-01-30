#!/usr/bin/perl
use strict;
use warnings;
use Data::Hexdumper;

my $AMAX_PATH = '/home/willow/prj/amax/amax-hg/Astromaximum';

require "$AMAX_PATH/tools.pm";

my $lang = $ARGV[0];

my @interpret_files = glob("$AMAX_PATH/Astromaximum/interpret/$lang/*.txt");

my %event_type_hash;

foreach my $infile (@interpret_files) {
	open (INF, "<$infile") or die "$!: $infile";
	my @buf = <INF>;
	close (INF);
	$buf[0] =~ /\!\!type\s*(\w+)/i;
	my $evt=$1;
	if ($evt eq 'EV_MSG' or $evt =~ /EV_DEGPASS\d+/) {
		next;
	}
	my $event_type = $tools::eventType{$evt};
	if ($event_type !~ /^\d+$/) {
		die "Event $evt not defined in $infile! Skipped";
		next;
	}
	$buf[2]=~/\!\!planet\s*(.+?)[\n\r]+/i;
	my $planet=$1;
	my $out = '';
	my $count = 0;
	warn "$infile\n";
	foreach my $line (@buf) {
		$line =~ s/\/\/.+//is;
		next if $line !~ /%[\d\s\,\-]+%/;
		$line =~ s/\s*\Z//is;
		#$line =~ s/\.+\Z//is if $evt ne 'EV_MSG';
		$line =~ s/.*?%(.*?)%\s*//is;
		my @param = split (/,/, $1);
		for (my $i = 0; $i < 3; ++$i) {
			if (!defined ($param[$i])) {
				$param[$i] = -1;
			}
			else {
				$param[$i] = int($param[$i]);
			}
		}
		$line =~ s/[\*\~\#\^\$\}\>\@\=\{]//g;
		$line =~ s/\|/<\/p><p>/g;
		$line =~ s/<p>--<\/p>/<hr\/>/g;
		$line =~ s/--/&#8212;/g;
		
		my $len = 0;
		do{
			use bytes; $len=length($line);
		};

#		$out .= pack('cnNc', 1, 2, 3, 0xff); # 
		$out .= pack('cnnnna*', $planet, $param[0], $param[1], $param[2], $len, $line);
		++$count;
	}
	my $len = 0;
	do{
		use bytes; $len=length($out);
	};
	$event_type_hash{$event_type} = [$out, $len, $count]; # data, length, event count
}

my $key_count = scalar(keys (%event_type_hash));
my $out = 'S&WA' . pack('n', $key_count);
my $abs_data_offset = 6 + $key_count * 7;
my @sorted_keys = sort {$a <=> $b} keys (%event_type_hash);
foreach (@sorted_keys) {
	$out .= pack ('cNn', $_, $abs_data_offset, $event_type_hash{$_}->[2]);
	$abs_data_offset += $event_type_hash{$_}->[1];
}
foreach (@sorted_keys) {
	$out .= $event_type_hash{$_}->[0];
}
print $out;

sub hex_dump   # \$payload, $hdrlen
{
	my ($payload_ref, $hdrlen) = @_;
	return Data::Hexdumper::hexdump(
		data           => $$payload_ref, # what to dump
		# NB number_format is deprecated
		number_format  => 'n',   # display as unsigned 'shorts'
		start_position => $hdrlen,   # start at this offset ...
		end_position   => length($$payload_ref)    # ... and end at this offset
	);
}
