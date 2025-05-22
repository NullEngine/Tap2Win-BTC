<?php
// Error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);
ob_start(); // Start output buffering

// Load database config from .env file
$env = parse_ini_file(__DIR__ . '/.env');

// Database connection details from .env
$server = $env['DB_SERVER'];
$username = $env['DB_USERNAME'];
$password = $env['DB_PASSWORD'];
$database = $env['DB_DATABASE'];

$conn = new mysqli($server, $username, $password, $database);

// Check for connection errors
if ($conn->connect_error) {
    die(json_encode(["success" => "0", "message" => "Connection failed: " . $conn->connect_error]));
}

// Check if POST data is set
if (isset($_POST['email'], $_POST['name'], $_POST['refer_code'])) {
    $email = $_POST['email'];
    $name = $_POST['name'];
    $refer_code = $_POST['refer_code'];

    $birth_date = "number"; // Placeholder, update if needed
    $currents_points = 5000;
    $total_withdraw_points = 0;
    $currents_referral_points = 0;
    $friend_comission = 0;
    $total_withdraw_dollars = 0.00;
    $url = 'https://yourdomain.com/api/ip.php';

    $sql = "SELECT email FROM users WHERE email='$email'; 
            SELECT refer_code FROM users WHERE refer_code='$refer_code';";

    if ($conn->multi_query($sql)) {
        if ($result = $conn->store_result()) {
            if ($result->num_rows == 0) {
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
                            
                            if ($ip_result && isset($ip_result['ip'], $ip_result['country'])) {
                                $ip = $ip_result['ip'];
                                $country = $ip_result['country'];
                                
                                $sql2 = "INSERT INTO users (name, email, birth_date, currents_points, refer_code, total_withdraw_points, total_withdraw_dollars, currents_referral_points, friend_comission, ip, country)
                                VALUES ('$name', '$email', '$birth_date', '$currents_points', '$refer_code', '$total_withdraw_points', '$total_withdraw_dollars', '$currents_referral_points', '$friend_comission', '$ip', '$country')";

                                if ($conn->query($sql2) === TRUE) {
                                    echo json_encode(["success" => "-1", "message" => "Registration successful"]);
                                } else {
                                    echo json_encode(["success" => "0", "message" => "Insert error!"]);
                                }
                            } else {
                                echo json_encode(["success" => "0", "message" => "Insert error2!"]);
                            }
                        } else {
                            echo json_encode(["success" => "0", "message" => "Refer code already exists!"]);
                        }
                        $result->free();
                    }
                }
            } else {
                echo json_encode(["success" => "1", "message" => "User found"]);
            }
            $result->free();
        }
    } else {
        echo json_encode(["success" => "0", "message" => "Query error"]);
    }
} else {
    echo json_encode(["success" => "0", "message" => "Missing required fields."]);
}

$conn->close();
