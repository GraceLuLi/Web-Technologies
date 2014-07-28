#!/usr/bin/perl -w 

print "Content-type: text/html\n\n";
print "<meta http-equiv=\"Content-Type\" content=\"text/html;  charset=UTF-8\"/>\n";
read(STDIN, $buffer, $ENV{'CONTENT_LENGTH'});

@pairs = split(/&/, $buffer);
foreach $pair (@pairs){
    ($name, $value) = split(/=/, $pair);
    $value =~ s/%([a-fA-F0-9])/pack("C", hex($1))/eg;
    push(@input, $value);
}


$url = "http://www.tripadvisor.com/Search?q="."$input[1]"."+"."$input[0]";

if(eval{require LWP::Simple;}){
}else{
print "You need to install the Perl LWP module!<br>";
exit;
}

$input[0] =~ tr/+/ /;
print "<html><head><title>hotel search</title><style type='text/css'>tr{text-align:center}</style></head><body><center>";
print "<h1><b>Search Result</b></h1>";
print "<h2><b>"."$input[1]"." hotel in "."$input[0]"."</b></h2>";

$content = LWP::Simple::get($url);
$content =~ m/<div class="SEARCH_RESULTS">((.|\n)*?)<\/div>/;
@parts = split(/<div class=\"searchResult srLODGING\">/, $content);
if($parts[1]){
#print "<table border='1' ><tr><td>Image</td><td>Name</td><td>Location</td><td>Rating out of 5</td><td>Reviews</td><tr>";
print "<table border='1' ><tr><td>hotel</td><td>Location</td><td>Rating out of 5</td><td>Reviews</td><tr>";

 $counts=0;
   foreach $part (@parts){
      if($counts==0){
      $counts++;
      }
      else{
      $part =~m/<div class="sizedThumb" style="(.+)">\n<img src="(.+)" class="photo_image" style="(.+)" alt="(.+)"\/>\n<\/div>/ig;
      #$hotelname = $4;
      
      $part =~ m/<div class="srSubHead((.|\n)*?)<\/div>/;
      $location =$1;
      $location =~s/((.|\n)*?)<span((.|\n)*?)\;//;
	  

      $part =~m/<div class="thumbnail">((.|\n)*?)<\/div>/;
      $c1 = $1;
      $src="<a href="."((.|\n)*?)"."<img src=\""."((.|\n)*?)"."\"";
      $c1 =~m/$src/;
      $Image = $3;
     
      $part =~m/<div class="rating">((.|\n)*?)<\/div>/;
      $c2 = $1;
      $c2=~m/href="(((?!").|\n)*?)"/;
      $reviewlink=$1;
     
      $c2 =~m/rate_no\sno((.|\n)*)"><img/;
      $rating =$1;   
      
      $c2 =~ /<\/span>((.|\n)*)reviews/;
      $reviews =$1;


	 # print "<tr><td><img width=\"100\" length=\"100\" height=\"100\" src=\""."$Image"."\"></td>"."<td>"."$hotelname"."</td>"."<td>"."$location"."</td>"."<td>"."$rating"."</td>"."<td><a href=\"http://www.tripadvisor.com"."$reviewlink"."\">"."$reviews"."</a></td></tr>";
	 print "<tr><td><img width=\"100\" length=\"100\" height=\"100\" src=\""."$Image"."\"></td>"."<td>"."$location"."</td>"."<td>"."$rating"."</td>"."<td><a href=\"http://www.tripadvisor.com"."$reviewlink"."\">"."$reviews"."</a></td></tr>";

     }
     }
      
      print "</table>";
      print "</center></body></html>";
}
else{
    print "<h><b>No hotels found!</b></h>";
    }
