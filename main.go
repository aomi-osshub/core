package main

import (
	"./entity"
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
)

func main() {
	r := gin.Default()
	r.GET("/ping", func(c *gin.Context) {
		c.JSON(200, gin.H{
			"message": "pong",
		})
	})

	r.GET("/", func(context *gin.Context) {
		log.Println("获取文件")
	})
	r.POST("/", func(context *gin.Context) {
		file, e := context.FormFile("file")

		if nil != e {
			context.JSON(http.StatusOK, gin.H{
				"status":   "3001",
				"describe": "file field is required",
			})
			return
		}

		log.Println(file.Filename)
		log.Println(file.Size)
		log.Println(file.Header)
		log.Println(context.Params)

		assets := entity.Assets{
			Name: file.Filename,
			Size: file.Size,
		}
		log.Println(assets)
		context.SaveUploadedFile(file, "hello.txt")

		context.JSON(http.StatusOK, gin.H{
			"status": "0000",
		})
	})
	_ = r.Run() // listen and serve on 0.0.0.0:8080
}
