<?php
$UserName = $_POST["UserName"];
$Password = $_POST["Password"];
$FullName = $_POST["Name"];

require 'DataBase.php';
if($con){
    $sql = "select * from user_information where UserName = '$UserName'";
    $result = mysqli_query($con,$sql);

    if(mysqli_num_rows($result)>0){
        $status = "ok";
        $result_code = 0;
        echo json_encode(array('status'=>$status,'result_code'=>$result_code));
    }
    else{
        $Password = md5($Password);
        $sql = "insert into user_information(FullName,UserName,Password) values('$FullName','$UserName','$Password')";
        if(mysqli_query($con,$sql)){
            $status = "ok";
            $result_code = 1;
            echo json_encode(array('status'=>$status,'result_code'=>$result_code));
        }
        else{
            $status = "failed";
            echo json_encode(array('status'=>$status),JSON_FORCE_OBJECT);
        }
    }
}
else{
    $status = "failed";
            echo json_encode(array('status'=>$status),JSON_FORCE_OBJECT);
}
?>