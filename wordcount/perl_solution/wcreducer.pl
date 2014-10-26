#!/usr/bin/perl
$sum = 0;
$last = "";
while (<>) {						#read lines from stdin
  ($key, $value) = split /\t/;	#read key and value
  $last = $key if ($last eq "");	#first time through
  if ($last ne $key) {				#has key has changed?
    print "$last\t" . ($sum) . "\n";	# if so output last key/value
    $last = $key;						# start with the new key
    $sum = 0;				    		# reset sum for the new key
  }
  $sum += $value;					# add value to tally sum for key
}
print "$key\t" . ($sum) . "\n";	# print the final pair