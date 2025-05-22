<?php
// Database connection - Change these to your own server info
$servername = "YOUR_SERVER_NAME";
$username = "YOUR_DB_USERNAME";
$password = "YOUR_DB_PASSWORD";
$dbname = "YOUR_DB_NAME";

$conn = new mysqli($servername, $username, $password, $dbname);
$result2 = array();
$withdraw_data = array();

if ($conn->connect_error) {
    $result2["success"] = "0";
    $result2["message"] = "Connection failed: " . $conn->connect_error;
    echo json_encode($result2);
} else {
    // Check if command is set
    if (isset($_POST['command'])) {
        $command = $_POST['command'];
        
        if ($command == 2) {
            // First logic: Fetch total commission and withdraw data

            if (!isset($_POST['friend_refer_code'])) {
                $result2["success"] = "0";
                $result2["message"] = "friend_refer_code is required.";
            } else {
                $friend_refer_code = $_POST['friend_refer_code'];

                // Multi query string with two SELECT queries
                $sql = "
                    SELECT SUM(friend_comission) AS total_commission FROM users WHERE friend_refer_code = '$friend_refer_code';
                    SELECT email, SUM(points) AS total_points, MIN(withdraw_id) AS smallest_id FROM withdraw WHERE friend_refer_code = '$friend_refer_code'
                    GROUP BY email ORDER BY smallest_id ASC LIMIT 50;";

                if ($conn->multi_query($sql)) {
                    // First result set (total commission)
                    if ($result = $conn->store_result()) {
                        if ($result->num_rows > 0) {
                            if ($row = $result->fetch_assoc()) {
                                $total_commission = (int)$row['total_commission'];
                                $result2['success_commission'] = "1";
                                $result2["total_commission"] = $total_commission;
                                $result2["message_commission"] = "Commission data found.";
                            }
                        } else {
                            $result2["success_commission"] = "0";
                            $result2["message_commission"] = "No commission data found.";
                        }
                        $result->free();
                    }

                    // Move to the next result set (withdraw data)
                    if ($conn->next_result()) {
                        if ($result = $conn->store_result()) {
                            if ($result->num_rows > 0) {
                                while ($row = $result->fetch_assoc()) {
                                    $withdraw_data[] = $row;
                                }
                                $result2['success_withdraw'] = "1";
                                $result2['message_withdraw'] = "Withdrawal data found.";
                                $result2['withdraw_data'] = $withdraw_data;
                            } else {
                                $result2["success_withdraw"] = "0";
                                $result2["message_withdraw"] = "No withdrawal data found.";
                            }
                            $result->free();
                        }
                    }

                    $result2["success"] = "1";
                    $result2["message"] = "Query executed successfully.";
                } else {
                    $result2["success"] = "0";
                    $result2["message"] = "Error executing multi query: " . $conn->error;
                }
                echo json_encode($result2);
            }
        } elseif ($command == 1) {
            // Second logic: Update friend_refer_code
            if (!isset($_POST['friend_refer_code']) || !isset($_POST['email'])) {
                $result2["success"] = "0";
                $result2["message"] = "Both friend_refer_code and email are required.";
            } else {
                $friend_refer_code = $_POST['friend_refer_code'];
                $email = $_POST['email'];
                $check_sql = "SELECT * FROM users WHERE refer_code = '$friend_refer_code'";
                $check_result = $conn->query($check_sql);
                
                if ($check_result->num_rows == 0) {
                    $result2["success"] = "0";
                    $result2["message"] = "Friend refer code does not exist.";
                } else {
                    $update_sql = "UPDATE users SET friend_refer_code = '$friend_refer_code' WHERE email = '$email'";
                    if ($conn->query($update_sql) === TRUE) {
                        $result2["success"] = "1";
                        $result2["message"] = "Friend refer code updated successfully.";
                    } else {
                        $result2["success"] = "0";
                        $result2["message"] = "Error updating friend refer code: " . $conn->error;
                    }
                }
                echo json_encode($result2);
            }    
        } else if ($command == 3) {

            if (!isset($_POST['email'])) {
                $result2["success"] = "0";
                $result2["message"] = "Email is required.";
            } else {
                $email = htmlspecialchars($_POST['email']);
                $conn->begin_transaction();

                try {   
                    $stmt = $conn->prepare("SELECT currents_points, currents_referral_points FROM users WHERE email = ?");
                    if ($stmt === false) {
                        throw new Exception("Failed to prepare statement");
                    }
                    $stmt->bind_param("s", $email);
                    $stmt->execute();
                    $check_result = $stmt->get_result();

                    if ($check_result->num_rows == 0) {
                        $result2["success"] = "0";
                        $result2["message"] = "User not found.";
                    } else {
                        $row = $check_result->fetch_assoc();
                        $current_points = $row['currents_points'];
                        $currents_referral_points = $row['currents_referral_points'];

                        if ($currents_referral_points > 0) {
                            $updated_points = $current_points + $currents_referral_points;
                            $current_referral_points = 0;

                            $update_stmt = $conn->prepare("UPDATE users SET currents_referral_points = ?, currents_points = ? WHERE email = ?");
                            $update_stmt->bind_param("iis", $current_referral_points, $updated_points, $email);

                            if ($update_stmt->execute()) {
                                $conn->commit();
                                $result2["success"] = "1";
                                $result2["message"] = "Points updated successfully.";
                            } else {
                                $conn->rollback();
                                $result2["success"] = "0";
                                $result2["message"] = "Error updating points.";
                            }
                        } else {
                            $result2["success"] = "0";
                            $result2["message"] = "You have no referral points.";
                        }
                    }
                } catch (Exception $e) {
                    $conn->rollback();
                    $result2["success"] = "0";
                    $result2["message"] = "Transaction failed: " . $e->getMessage();
                } finally {
                    $stmt->close();
                    if (isset($update_stmt)) $update_stmt->close();
                }
            }

            echo json_encode($result2);

        } else {
            $result2["success"] = "0";
            $result2["message"] = "Invalid command.";
            echo json_encode($result2);
        }
    } else {
        $result2["success"] = "0";
        $result2["message"] = "Command is required.";
        echo json_encode($result2);
    }
}

// Close connection
$conn->close();
?>
