<?php
$user_name = "root";
$password = "";
$dbname = "money_management";
$host_name = "localhost";
$con = mysqli_connect($host_name, $user_name, $password, $dbname);

$userID = $_GET['UserID']; // Nhận UserID từ yêu cầu GET

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    // Retrieve data from the "bill" table
    $queryBill = "SELECT * FROM bill WHERE UserID = ?";
    $stmtBill = mysqli_prepare($con, $queryBill);
    mysqli_stmt_bind_param($stmtBill, 'i', $userID);
    mysqli_stmt_execute($stmtBill);
    $resultBill = mysqli_stmt_get_result($stmtBill);

    // Retrieve data from the "category" table
    $queryCategory = "SELECT * FROM category WHERE UserID = ?";
    $stmtCategory = mysqli_prepare($con, $queryCategory);
    mysqli_stmt_bind_param($stmtCategory, 'i', $userID);
    mysqli_stmt_execute($stmtCategory);
    $resultCategory = mysqli_stmt_get_result($stmtCategory);

    if ($resultBill && $resultCategory) {
        $dataBill = array();
        $dataCategory = array();

        // Fetch data from "bill" table and store in an array
        while ($rowBill = mysqli_fetch_assoc($resultBill)) {
            $dataBill[] = $rowBill;
        }

        // Fetch data from "category" table and store in an array
        while ($rowCategory = mysqli_fetch_assoc($resultCategory)) {
            $dataCategory[] = $rowCategory;
        }

        // Return JSON response with the retrieved data
        echo json_encode(array("response" => "OK", "billdata" => $dataBill, "categorydata" => $dataCategory));
    } else {
        $status = 'FAILED: ' . mysqli_error($con);
        // Return JSON response
        echo json_encode(array("response" => $status));
    }
}
?>
