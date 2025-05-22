<?php
// Change these variables according to your own database configuration
$server = "localhost";            // Database server (usually localhost)
$username = "your_db_username";   // Your database username
$password = "your_db_password";   // Your database password
$database = "your_db_name";       // Your database name

$conn = new mysqli($server, $username, $password, $database);

// Check for connection errors
if ($conn->connect_error) {
    die(json_encode(["success" => "0", "message" => "Connection failed: " . $conn->connect_error]));
}

$sql = "SELECT * FROM app_info WHERE post_id = (SELECT MAX(post_id) FROM app_info)";

if ($conn->query($sql)) {
    $result2["success"] = "1";
    $result2["message"] = ""; 
    echo json_encode($result2); 
} else {
    $result2["success"] = "0";
    $result2["message"] = ""; 
    echo json_encode($result2); 
}
?>
