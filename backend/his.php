<?php

// Database connection details - replace with your own server info
$servername = "YOUR_SERVER_NAME";
$username = "YOUR_DB_USERNAME";
$password = "YOUR_DB_PASSWORD";
$dbname = "YOUR_DB_NAME";

// Create a new MySQLi connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Initialize arrays to hold response data
$withdraw_data = array();
$response = array();

// Check connection
if ($conn->connect_error) {
    // Connection failed - respond with error message
    $response["success"] = "0";
    $response["message"] = "Connection failed: " . $conn->connect_error;
    echo json_encode($response);
} else {
    // Check if 'email' POST parameter is set
    if (!isset($_POST['email'])) {
        // Email not provided in POST request - respond with error
        $response["success"] = "0";
        $response["message"] = "Email is required.";
        echo json_encode($response);
    } else {
        // Get the email from POST data, safe to use after validation
        $email = $_POST['email'];

        // Prepare SQL query to fetch withdraw records for the given email
        $sql = "SELECT email, points, status, btc, time FROM withdraw WHERE email='$email' ORDER BY withdraw_id DESC";

        // Execute the query
        if ($result = $conn->query($sql)) {
            // Check if any rows returned
            if ($result->num_rows > 0) {
                // Fetch each row and add to withdraw_data array
                while ($row = $result->fetch_assoc()) {
                    $withdraw_data[] = $row;
                }
                // Withdraw data found - respond with success and data
                $response['success_withdraw'] = "1";
                $response['message_withdraw'] = "Withdraw data found.";
                $response['withdraw_data'] = $withdraw_data;
            } else {
                // No withdraw data found for this email
                $response['success_withdraw'] = "0";
                $response['message_withdraw'] = "No withdraw data found.";
            }

            // Indicate query executed successfully
            $response["success"] = "1";
            $response["message"] = "Query executed successfully.";
        } else {
            // Query execution error - respond with error message
            $response["success"] = "0";
            $response["message"] = "Query error: " . $conn->error;
        }

        // Send JSON response
        echo json_encode($response);
    }
}

?>
