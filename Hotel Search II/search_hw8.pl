#!/usr/bin/perl -w 

print "Content-type: text/xml\n\n";
print "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n";
$request_method = $ENV{'REQUEST_METHOD'}; 
if ($request_method eq "GET") 
 {
  $buffer=$ENV{'QUERY_STRING'}; 
 } 
 else 
 { 
   $size_of_form_info=$ENV{'CONTENT_LENGTH'}; 
   read(STDIN, $buffer, $size_of_form_info); 
 }

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


$content = LWP::Simple::get($url);
$content =~ m/<div class="SEARCH_RESULTS">((.|\n)*?)<\/div>/;
@parts = split(/<div class=\"searchResult srLODGING\">/, $content);
$length = @parts-1;

$totallength='<hotels total="'.$length.'">';
print "$totallength";


if($parts[1]){
 $counts=0;
   foreach $part (@parts){
      if($counts==0){
      $counts++;
      }
      else{
      #$part =~m/<div class="sizedThumb" style="(.+)">\n<img src="(.+)" class="photo_image" style="(.+)" alt="(.+)"\/>\n<\/div>/ig;#old useful part
      $part =~m/<div class="sizedThumb" style="(.+)">\n<img class="photo_image" width="(.+)" height="(.+)" alt="(.+)"\/>\n<\/div>/ig;
      #newpart add on 07/10/2012
      
      $hotelname = $4; #old useful part
     
      
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
      $reviewurl= 'http://www.tripadvisor.com'.$reviewlink;
     
      $c2 =~m/rate_no\sno((.|\n)*)"><img/;
      $rating =$1; 
      $ratingof5 =$rating/10;
      
      $c2 =~ /<\/span>((.|\n)*)reviews/;
      $reviews =$1;
      
     #$contentofpart = '<hotel name="'.$hotelname.'"/>';
     $hotelname =~ s/\&/\&amp;/;
     $contentofpart = '<hotel name="'.$hotelname.'" location="'.$location.'"  no_of_stars="'.$ratingof5.'"  no_of_reviews="'.$reviews.'"  image_url="'.$Image.'"  review_url="'.$reviewurl.'"/>';
     
     print "$contentofpart";
    
     
     
     }
     }   
    
}

else{

    }
print "</hotels>";