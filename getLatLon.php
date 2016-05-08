<?php

$objConnect = mysql_connect("localhost","root","11111111");
$objDB = mysql_select_db("test");

$strSQL = "SELECT * FROM `location`  ORDER BY LocationID  ASC ";
$objQuery = mysql_query($strSQL) or die(mysql_error());
$arrRows = array();
$arryItem = array();
while($arr = mysql_fetch_array($objQuery)) {
$arryItem["LocationID"] = $arr["LocationID"];
$arryItem["Latitude"] = $arr["Latitude"];
$arryItem["Longitude"] = $arr["Longitude"];
$arryItem["LocationName"] = $arr["LocationName"];
$arrRows[] = $arryItem;
}
echo json_encode($arrRows);
?>
