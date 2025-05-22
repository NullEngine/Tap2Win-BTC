<?php
// ✅ Database connection (change credentials as needed)
$servername = "localhost";
$username = "root";
$password = "";  // Set your password if needed
$dbname = "mydatabase";

// ✅ Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// ✅ Response arrays
$withdraw_data = array();
$user_data = array();
$points_data = array();
$response = array();

// ✅ Check connection
if ($conn->connect_error) {
    $response["success"] = "0";
    $response["message"] = "Connection failed: " . $conn->connect_error;
    echo json_encode($response);
    exit;
}

// ✅ Command check
if (isset($_POST['command'])) {
    $command = $_POST['command'];

    // 🔹 Command 1: Get user data by email
    if ($command == 1) {
        if (!isset($_POST['email'])) {
            $response["success"] = "0";
            $response["message"] = "Email is required.";
        } else {
            $email = $_POST['email'];
            $sql = "SELECT currents_referral_points, currents_points, refer_code FROM users WHERE email = '$email'";
            $result = $conn->query($sql);

            if ($result && $result->num_rows > 0) {
                while ($row = $result->fetch_assoc()) {
                    $user_data[] = $row;
                }
                $response["success_userdata"] = "1";
                $response["message_userdata"] = "User data found.";
                $response["user_data"] = $user_data;
            } else {
                $response["success_userdata"] = "0";
                $response["message_userdata"] = "No user data found.";
            }

            $response["success"] = "1";
            $response["message"] = "Query executed.";
        }
        echo json_encode($response);
    }

    // 🔹 Command 2: Last 3 withdraws
    else if ($command == 2) {
        $sql = "SELECT email, points FROM withdraw ORDER BY withdraw_id DESC LIMIT 3";
        $result = $conn->query($sql);

        if ($result && $result->num_rows > 0) {
            while ($row = $result->fetch_assoc()) {
                $withdraw_data[] = $row;
            }
            $response["success_withdraw"] = "1";
            $response["message_withdraw"] = "Withdraw data found.";
            $response["withdraw_data"] = $withdraw_data;
        } else {
            $response["success_withdraw"] = "0";
            $response["message_withdraw"] = "No withdraw data found.";
        }

        $response["success"] = "1";
        $response["message"] = "Query executed.";
        echo json_encode($response);
    }

    // 🔹 Command 3: Update points if matched with database
    else if ($command == 3) {
        if (!isset($_POST['new_points']) || !isset($_POST['email']) || !isset($_POST['id'])) {
            $response["success"] = "0";
            $response["message"] = "Required fields missing.";
        } else {
            $new_points = filter_var($_POST['new_points'], FILTER_VALIDATE_INT);
            $email = $_POST['email'];
            $points_id = $_POST['id'];

            $conn->begin_transaction();

            try {
                $stmt = $conn->prepare("SELECT currents_points FROM users WHERE email = ?");
                $stmt->bind_param("s", $email);
                $stmt->execute();
                $user_result = $stmt->get_result();

                if ($user_result->num_rows == 0) {
                    $response["success"] = "0";
                    $response["message"] = "User does not exist.";
                } else {
                    $current_points = $user_result->fetch_assoc()['currents_points'];

                    $stmt2 = $conn->prepare("SELECT point FROM points_table WHERE points_id = ?");
                    $stmt2->bind_param("i", $points_id);
                    $stmt2->execute();
                    $points_result = $stmt2->get_result();

                    if ($points_result->num_rows == 0) {
                        $response["success"] = "0";
                        $response["message"] = "Point ID not found.";
                    } else {
                        $valid_point = $points_result->fetch_assoc()['point'];
                        if ($valid_point == $new_points) {
                            $updated_points = $current_points + $new_points;

                            $update_stmt = $conn->prepare("UPDATE users SET currents_points = ? WHERE email = ?");
                            $update_stmt->bind_param("is", $updated_points, $email);
                            if ($update_stmt->execute()) {
                                $conn->commit();
                                $response["success"] = "1";
                                $response["message"] = "Points updated.";
                            } else {
                                $conn->rollback();
                                $response["success"] = "0";
                                $response["message"] = "Update failed.";
                            }
                        } else {
                            $response["success"] = "0";
                            $response["message"] = "Point mismatch!";
                        }
                    }
                }
            } catch (Exception $e) {
                $conn->rollback();
                $response["success"] = "0";
                $response["message"] = "Transaction failed: " . $e->getMessage();
            }

            echo json_encode($response);
        }
    }

    // 🔹 Command 4: Fetch 20 random points based on region using IP API
    else if ($command == 4) {
        $url = 'https://yourserver.com/ip.php';  // <-- Replace with your actual server URL
        $userIP = $_SERVER['REMOTE_ADDR'];
        $context = stream_context_create(["http" => ["header" => "X-Forwarded-For: $userIP\r\n"]]);
        $ip_json = file_get_contents($url, false, $context);
        $ip_data = json_decode($ip_json, true);

        if ($ip_data && isset($ip_data['key'])) {
            $region = ($ip_data['key'] == 1) ? "euus" : "world";
            $sql = "SELECT * FROM points_table WHERE region='$region' ORDER BY RAND() LIMIT 20";
            $result = $conn->query($sql);

            if ($result && $result->num_rows >= 20) {
                while ($row = $result->fetch_assoc()) {
                    $points_data[] = $row;
                }
                $response["success_point"] = "1";
                $response["message_point"] = "Points found.";
                $response["points_data"] = $points_data;
            } else {
                $response["success_point"] = "0";
                $response["message_point"] = "Not enough data.";
            }

            $response["success"] = "1";
            $response["message"] = "Query executed.";
        } else {
            $response["success"] = "0";
            $response["message"] = "Failed to get IP data.";
        }

        echo json_encode($response);
    }

    // 🔹 Command 5: Placeholder (not implemented)
    else if ($command == 5) {
        $response["success"] = "0";
        $response["message"] = "Command 5 is under development.";
        echo json_encode($response);
    }

} else {
    $response["success"] = "0";
    $response["message"] = "No command provided.";
    echo json_encode($response);
}
?>
