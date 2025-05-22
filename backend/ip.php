<?php

// Load Composer's autoload to access GeoIP2 classes
require_once '/home/swbnwisr/public_html/tap/vendor/autoload.php';

use GeoIp2\Database\Reader;

try {
    // Initialize GeoIP2 reader with the MaxMind database file path
    $reader = new Reader('/home/swbnwisr/public_html/tap/geoip/GeoLite2-City.mmdb');
    
    // Get client IP address from HTTP headers; fallback to 'Unknown' if not found
    // Note: HTTP_X_FORWARDED_FOR may contain multiple IPs, take the first if needed
    $ip = $_SERVER['HTTP_X_FORWARDED_FOR'] ?? 'Unknown';

    // Get location record for the IP address
    $record = $reader->city($ip);  
    
    // Extract country and city names from the GeoIP record
    $country = $record->country->name; 
    $city = $record->city->name; 
    
    // Prepare the result array with city, country, and IP address
    $result['city'] = $city;
    $result['country'] = $country;
    $result['ip'] = $ip;

    // Check if the country is one of the specified North American or European countries
    if (in_array($country, ["United States", "Canada", "United Kingdom", "France", "Germany", "Italy"])) {
        $result['key'] = 1;
        $result['msg'] = "From North America or Europe!";
    } else {
        $result['key'] = 0;
        $result['msg'] = "From another region!";
    }

} catch (Exception $e) {
    // Handle errors such as IP not found in database or invalid IP
    $result['key'] = -1;
    $result['msg'] = "Not Found! " . $e->getMessage();
}

// Return the results as a JSON response
echo json_encode($result);

?>
