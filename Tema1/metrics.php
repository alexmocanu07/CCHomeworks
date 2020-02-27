<?php
$fn = fopen('./log_'.date("j.n.Y").'.txt', "r");

while (!feof($fn)) {
    $result = fgets($fn);
?><p> <?php echo $result;?> </p><?php
}

fclose($fn);

