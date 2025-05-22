<?php
// Error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);
ob_start(); // Start output buffering

// Database connection details - CHANGE THESE TO YOUR SERVER INFO
$server = "your_server_host";       // e.g. "localhost"
$username = "your_database_user";   // e.g. "root"
$password = "your_database_pass";   // e.g. "password123"
$database = "your_database_name";   // e.g. "mydatabase"
$conn = new mysqli($server, $username, $password, $database);

// Check for connection errors
if ($conn->connect_error) {
    die(json_encode(["success" => "0", "message" => "Connection failed: " . $conn->connect_error]));
}

// Check if POST data is set
if (isset($_POST['email'], $_POST['name'], $_POST['refer_code'])) {
    // Get POST data
    $email = $_POST['email'];
    $name = $_POST['name'];
    $refer_code = $_POST['refer_code'];
    $birth_date = "number"; // You might want to change this to a valid date
    $currents_points = 5000;
    $total_withdraw_points = 0;
    $currents_referral_points = 0;
    $friend_comission = 0;
    $total_withdraw_dollars = 0.00;

    // CHANGE THIS TO YOUR NEW URL
    $url = 'https://yourdomain.com/path/to/ip.php';

    // SQL query to check for existing user and refer code
    $sql = "SELECT email FROM users WHERE email='$email'; 
            SELECT refer_code FROM users WHERE refer_code='$refer_code';";

    if ($conn->multi_query($sql)) {
        // First result set
        if ($result = $conn->store_result()) {
            if ($result->num_rows == 0) {
                // No existing user, check refer code
                if ($conn->more_results() && $conn->next_result()) {
                    if ($result = $conn->store_result()) {
                        if ($result->num_rows == 0) {
                            
                            $userIP = $_SERVER['REMOTE_ADDR']; 
                            $contextOptions = ["http" => [
                                "header" => "X-Forwarded-For: $userIP\r\n"
                            ]];
                            $context = stream_context_create($contextOptions);
                            $response = file_get_contents($url, false, $context);

                            $ip_result = json_decode($response, true); 
                            
                            // Insert new user

                            if($ip_result && isset($ip_result['ip'], $ip_result['country'])){
                                $ip=  $ip_result['ip'];
                                $country= $ip_result['country'];
                                $sql2 = "INSERT INTO users (name, email, birth_date, currents_points, refer_code, total_withdraw_points, total_withdraw_dollars, currents_referral_points, friend_comission, ip, country)
                                VALUES ('$name', '$email', '$birth_date', '$currents_points', '$refer_code', '$total_withdraw_points', '$total_withdraw_dollars', '$currents_referral_points', '$friend_comission', '$ip', '$country')";

                                if ($conn->query($sql2) === TRUE) {
                                    $result2["success"] = "-1";
                                    $result2["message"] = "Registration successful"; 
                                    echo json_encode($result2); 
                                } else {
                                    $result2["success"] = "0";
                                    $result2["message"] = "Insert error!"; 
                                    echo json_encode($result2); 
                                }
                            } else {
                                $result2["success"] = "0";
                                $result2["message"] = "Insert error due to IP info!"; 
                                echo json_encode($result2); 
                            }
                            
                        } else {
                            // Refer code already exists
                            $result2["success"] = "0";
                            $result2["message"] = "Refer code already exists!";
                            echo json_encode($result2);  
                        }
                        $result->free();
                    }
                }
            } else {
                // User already exists
                $result2["success"] = "1";
                $result2["message"] = "User found";
                echo json_encode($result2); 
            }
            $result->free();
        }
    } else {
        // Query execution failed
        $result2["success"] = "0";
        $result2["message"] = "Query error"; 
        echo json_encode($result2); 
    }
} else {
    // POST data is missing
    $result2["success"] = "0";
    $result2["message"] = "Missing required fields.";
    echo json_encode($result2);
}

$conn->close();
?>
