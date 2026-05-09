[Console]::InputEncoding  = [System.Text.UTF8Encoding]::new()
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new()

$guid = [guid]::NewGuid()

Write-Host "GUID SESSION:"  $guid

while ($true) {

    $prompt = Read-Host "`nYOU"

    if ($prompt -eq "exit") {
        break
    }

    $body = @{
        sessionId = $guid
        message   = $prompt
    } | ConvertTo-Json -Compress

    $bytes = [System.Text.Encoding]::UTF8.GetBytes($body)

    $tmpFile = New-TemporaryFile
    [System.IO.File]::WriteAllBytes($tmpFile, $bytes)

    Write-Host "`n" 
	
	curl.exe -s -N `
      -H "Accept: text/event-stream; charset=utf-8" `
      -H "Content-Type: application/json; charset=utf-8" `
      -X POST `
      http://localhost:8081/chat/stream `
      --data-binary "@$tmpFile" |

	ForEach-Object {

        if ($_ -match '^data:(.*)$') {

            $content = $matches[1].Trim()

            Write-Host $content
        }
    }

    Remove-Item $tmpFile

    Write-Host ""
}