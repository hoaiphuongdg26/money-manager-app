<?php
$user_name = "root";
$password = "";
$dbname = "money_management";
$host_name = "localhost";
$con = mysqli_connect($host_name, $user_name, $password, $dbname);

// Check connection
if (!$con) {
    die("Connection failed: " . mysqli_connect_error());
}

// Check if data is sent from the client
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Check if the variables are set
    $note = isset($_POST['note']) ? mysqli_real_escape_string($con, $_POST['note']) : '';
    $timecreateString = isset($_POST['timecreate']) ? $_POST['timecreate'] : '';
    $expense = isset($_POST['expense']) ? $_POST['expense'] : '';
    $categoryID = isset($_POST['categoryID']) ? $_POST['categoryID'] : '';
    $userID = isset($_POST['userID']) ? $_POST['userID'] : '';

        //1: co du lieu -> Insert vo db
        //2: k co du lieu -> Fail
        
    // Convert the datetime string to a DateTime object
    $timecreate = DateTime::createFromFormat('Y-m-d H:i:s', $timecreateString);

    // Check if the conversion was successful
    if ($timecreate === false) {
        // Handle the error, perhaps log it
        $status = 'FAILED: Invalid datetime format';
    } else {
        // Format the DateTime object to the required format for MySQL DATETIME
        $formattedDatetime = $timecreate->format('Y-m-d H:i:s');

        // Insert data into the database
        $sql = "INSERT INTO bill (Note, TimeCreate, Expense, CategoryID, UserID) 
                VALUES ('$note', '$formattedDatetime', '$expense', '$categoryID', '$userID')";

        if (mysqli_query($con, $sql)) {
            $status = 'OK';
		    $query = "SELECT * FROM bill";
   		    $result = mysqli_query($con, $query);

    		if ($result) {
        		$data = array();
        		while ($row = mysqli_fetch_assoc($result)) {
            		$data[] = $row;
                }
		        // Return JSON response with the retrieved data
        	    echo json_encode(array("response" => "OK", "data" => $data));
		    }else {
        	    $status = 'FAILED: ' . mysqli_error($con);
		        // Return JSON response
		        echo json_encode(array("response" => $status));
            }
        }else {
            $status = 'FAILED: ' . mysqli_error($con);
		    // Return JSON response
		    echo json_encode(array("response" => $status));
        }
    }
} 
else {
    //If the method is GET
    if($_SERVER['REQUEST_METHOD'] == 'GET'){
        // Retrieve all data from the "bill" table
        $query = "SELECT * FROM bill";
        $result = mysqli_query($con, $query);

        if ($result) {
            $data = array();

            // Fetch data and store in an array
            while ($row = mysqli_fetch_assoc($result)) {
                $data[] = $row;
            }
            // Return JSON response with the retrieved data
            echo json_encode(array("response" => "OK", "billdata" => $data));
        } else {
            $status = 'FAILED: ' . mysqli_error($con);
            // Return JSON response
            echo json_encode(array("response" => $status));
        }
    }
}
// Close connection
mysqli_close($con);
?>
