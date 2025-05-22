<?php
// Update these with your actual server details
$servername = "YOUR_SERVER_NAME";
$username = "YOUR_DB_USERNAME";
$password = "YOUR_DB_PASSWORD";
$dbname = "YOUR_DB_NAME";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    $result2["success"] = "0";
    $result2["message"] = "Connection failed: " . $conn->connect_error;
} else {
    if (isset($_POST['command'])) {
        $command = $_POST['command'];
        $cacheFile = 'btc_price_cache.txt';
        $cacheTime = 500; 
        if (!file_exists($cacheFile) || (time() - filemtime($cacheFile) > $cacheTime)) {
            $btcto_dollar = getBitcoinPriceFromCoingecko();
            file_put_contents($cacheFile, $btcto_dollar);
        } else {
            $btcto_dollar= file_get_contents($cacheFile);
        }
        
        $minimum = 10000;
        $one_dollar_to_points=100000;                     
        if ($command == 1) {
            if (!isset($_POST['email']) || !isset($_POST['input'])) {
                $result2["success"] = "0";
                $result2["message"] = "Both are required.";
            } else {
                $email = htmlspecialchars($_POST['email']);
                $input = (int) $_POST['input'];

                $conn->begin_transaction();

                try {
                    $stmt = $conn->prepare("SELECT currents_points, friend_refer_code, total_withdraw_points, total_withdraw_dollars, friend_comission FROM users WHERE email = ?");
                    if ($stmt === false) {
                        throw new Exception("Failed to prepare statement.");
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
                        $friend_refer_code = $row['friend_refer_code'];
                        $total_withdraw_points = $row['total_withdraw_points'];
                        $total_withdraw_dollars = $row['total_withdraw_dollars'];
                        $friend_comission = $row['friend_comission'];

                        $total_withdraw_update_points = $total_withdraw_points;
                        $total_withdraw_update_dollars = $total_withdraw_dollars;
                        $friend_update_comission = $friend_comission;
                        $dollar = (double) ($input / $one_dollar_to_points); 
                        
                        $current_refer_commission = (($input / 100) * 20);
                        $btc = (double) ($input / $one_dollar_to_points) / $btcto_dollar;
                        if ($current_points >= $input) {
                            if ($input < $minimum) {
                                $result2["success"] = "0";
                                $result2["message"] = "The minimum claim amount is 10000 points.";
                            } else {
                                if (empty($friend_refer_code)) {
                                    $result2["refercode_success"] = "0";
                                    $result2["refercode_message"] = "User has not entered a friend code.";
                                } else {
                                    $stmt_find_friend = $conn->prepare("SELECT currents_referral_points FROM users WHERE refer_code = ?");
                                    if ($stmt_find_friend === false) {
                                        throw new Exception("Failed to prepare find friend statement.");
                                    }
                                    $stmt_find_friend->bind_param("s", $friend_refer_code);
                                    $stmt_find_friend->execute();
                                    $find_result = $stmt_find_friend->get_result();

                                    if ($find_result->num_rows == 0) {
                                        $result2["refercode_success"] = "0";
                                        $result2["refercode_message"] = "Referral code not found.";
                                    } else {
                                        $friend_currents_referral_points = $find_result->fetch_assoc()['currents_referral_points'];
                                        $friend_update_referral_points = $friend_currents_referral_points + $current_refer_commission;

                                        $friend_comision_update_stmt = $conn->prepare("UPDATE users SET currents_referral_points = ? WHERE refer_code = ?");
                                        $friend_comision_update_stmt->bind_param("is", $friend_update_referral_points, $friend_refer_code);

                                        $total_withdraw_update_points = $input + $total_withdraw_points;
                                        $total_withdraw_update_dollars = (double) ($total_withdraw_dollars + $dollar);
                                        $friend_update_comission = $friend_comission + $current_refer_commission;

                                        if ($friend_comision_update_stmt->execute()) {
                                            $result2["refercode_success"] = "1";
                                            $result2["refercode_message"] = "Friend commission updated successfully.";
                                        } else {
                                            $conn->rollback();
                                            $result2["refercode_success"] = "0";
                                            $result2["refercode_message"] = "Error updating friend's points.";
                                        }
                                    }
                                }

                                $updated_points = $current_points - $input;
                                $update_stmt = $conn->prepare("UPDATE users SET currents_points = ?, total_withdraw_points = ?, total_withdraw_dollars = ?, friend_comission = ? WHERE email = ?");
                                $update_stmt->bind_param("iidis", $updated_points, $total_withdraw_update_points, $total_withdraw_update_dollars, $friend_update_comission, $email);

                                if ($update_stmt->execute()) {
                                    $result2["point_success"] = "1";
                                    $result2["point_message"] = "Points updated successfully.";
                                } else {
                                    $conn->rollback();
                                    $result2["point_success"] = "0";
                                    $result2["point_message"] = "Error updating points.";
                                }

                                $status = 0;

                                $stmt_withdraw_request = $conn->prepare("INSERT INTO withdraw (points, email, dollar, friend_refer_code, status, btc) VALUES (?,?,?,?,?,?)");
                                $stmt_withdraw_request->bind_param("isdsid", $input, $email, $dollar, $friend_refer_code, $status, $btc);

                                if ($stmt_withdraw_request->execute()) {
                                    $conn->commit();
                                    $result2["success"] = "1";
                                    $result2["message"] = "Request sent successfully.\n\nReview will be completed within 1 minute to 24 hours.";
                                } else {
                                    $conn->rollback();
                                    $result2["success"] = "0";
                                    $result2["message"] = "Error claiming points." . $stmt_withdraw_request->error;
                                }
                            }
                        } else {
                            $result2["success"] = "0";
                            $result2["message"] = "Insufficient points!";
                        }
                    }
                } catch (Exception $e) {
                    $conn->rollback();
                    $result2["success"] = "0";
                    $result2["message"] = "Transaction failed: " . $e->getMessage();
                } finally {
                    $stmt->close();
                    if (isset($update_stmt)) $update_stmt->close();
                    if (isset($stmt_withdraw_request)) $stmt_withdraw_request->close();
                }
            }
        } else if ($command == 2) {
            if (!isset($_POST['input'])) {
                $result2["success"] = "0";
                $result2["message"] = "INPUT NEEDED!";
            } else {
                $input = (int) $_POST['input'];

                $output = (double) ($input / $one_dollar_to_points) / $btcto_dollar;
                 

                $result2["convert"] = $output;
                $result2["success"] = "1";
                $result2["message"] = "Conversion successful.";
            }
        } else {
            $result2["success"] = "0";
            $result2["message"] = "Invalid command.";
        }
    } else {
        $result2["success"] = "0";
        $result2["message"] = "Command is required.";
    }
}

function getBitcoinPriceFromCoingecko() {
    // URL to fetch Bitcoin price in USD
    $url = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd";
    $json = file_get_contents($url);
    $data = json_decode($json, true);

    return $data['bitcoin']['usd'];
}

echo json_encode($result2);

$conn->close();
?>
