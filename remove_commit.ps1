$rebaseFile = $args[0]
$content = Get-Content $rebaseFile
$newContent = $content | Where-Object { $_ -notmatch '^pick 775acbf' }
Set-Content -Path $rebaseFile -Value $newContent

