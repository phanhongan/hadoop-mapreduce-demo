#!/usr/bin/env perl
while (<>) {					# Read lines from stdin
  chomp; 						# Get rid of the trailing newline
  (@words) = split /\s+/;	# Create an array of words
  foreach $w (@words) { 	# Loop through the array
    print "$w\t1\n"; 		# Print out the key and value
  }
}
