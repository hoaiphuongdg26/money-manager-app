<?php
$UserName = $_POST["UserName"];
$Password = $_POST["Password"];

require 'DataBase.php';
if($con){
    $sql = "select * from user_information where UserName = '$UserName'";
    $result = mysqli_query($con,$sql);

    if(mysqli_num_rows($result)>0){
        $row = mysqli_fetch_assoc($result);
        $dbPassword = $row['Password'];
        if(md5($Password)==$dbPassword){
            $status = "ok";
            $result_code = 1;
            $name = $row['FullName'];
        echo json_encode(array('status'=>$status,'result_code'=>$result_code,'name'=>$name));
        }
        else{
            $status = "ok";
            $result_code = 0;
            echo json_encode(array('status'=>$status,'result_code'=>$result_code));
        }
    }
    else{
        $status = "ok";
        $result_code = 0;
        echo json_encode(array('status'=>$status,'result_code'=>$result_code));
    }
}
else{
    $status = "failed";
    echo json_encode(array('status'=>$status),JSON_FORCE_OBJECT);
}
?>