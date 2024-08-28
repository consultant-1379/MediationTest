#!/usr/bin/perl -w
local $/;

open(DAT, "/opt/jboss-eap/standalone/configuration/standalone-full-ha.xml") || die("Could not open file!");
my $content = <DAT>;
close(DAT);
$content =~ s/<deployments>.*?<\/deployments>/<deployments><\/deployments>/sg;
print $content;
