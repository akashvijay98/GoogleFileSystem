# GoogleFileSystem

the aim of this project is to implement google file system, where a user adds a file to gfs, the file is broken into chunks and then sent to different chunk servers via sockets.
The file metadata are stored using Postgresql database.


Step1: Go to Docker1 and Docker2 Directories and open new terminal.In the terminal type the following command: 
       docker build -t ImageName .  
       docker run -d -p 4592:4592 -v C:/Users/ajayv/Documents/Test2:/temp2 ImageName

       Note: you can edit port no. and other configurations in the DockerFile in the Docker1 and Docker2 directories.

Step2: Go inside the src Directory and open new terminal and type the following:
       docker compose up to run the database instance.

Step3: start the application.

Step4: Open postman:
    step4.0.0: create a POST request with the URL -> localhost:8080/gfs
    step4.0.1: give the fileName, fileSize and filePath in the payload
              Example payload:  
                {
                  "file":"C:/Users/ajayv/Documents/testvideob.mp4",
                  "fileName": "testvideob",
                  "fileExtension": ".mp4"
                }

              once the file gets uploaded, you will get a resopse: "Successfully Uploaded"

  step4.1.0: to get the file, get the fileId from the file database, and then create a GET Request in Postman with the following endpoint: localhost:8080/gfs
  step 4.1.1: give the fileID, fileName and fileExtension as params.
            example: localhost:8080/gfs?fileId=20cbeae9-abae-434a-a7c3-230fb7b19323&fileName=testvideob&fileExtension=.mp4

  step 4.1.2: you will find the downlaoded file inside your document folder.

  


