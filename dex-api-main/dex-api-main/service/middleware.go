package service

import (
	"DexAPI/utils/log"
	"fmt"
	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
	"net/http"
	"runtime/debug"
)

func cors() gin.HandlerFunc {
	return func(c *gin.Context) {
		method := c.Request.Method
		origin := c.Request.Header.Get("Origin")
		if origin != "" {
			c.Header("Access-Control-Allow-Origin", "*") // 可将将 * 替换为指定的域名
			c.Header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, UPDATE")
			c.Header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
			c.Header("Access-Control-Expose-Headers", "Content-Length, Access-Control-Allow-Origin, Access-Control-Allow-Headers, Cache-Control, Content-Language, Content-Type")
			c.Header("Access-Control-Allow-Credentials", "true")
		}
		if method == "OPTIONS" {
			c.AbortWithStatus(http.StatusNoContent)
		}
		c.Next()
	}
}

func (s *Service) bucket() gin.HandlerFunc {
	return func(c *gin.Context) {
		if s.buckets.TakeAvailable(1) == 0 {
			CxtResponse(c, "limit")

			c.Abort()
			return
		}

		c.Next()
	}
}

func (s *Service) panicMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		defer func() {

			if r := recover(); r != nil {
				stackTrace := fmt.Sprintf("%s", r) // 将错误转换为字符串形式
				log.Error("panic", zap.String("err", stackTrace), zap.String("stack", string(debug.Stack())))
			}
		}()

		c.Next()
	}
}

func (s *Service) singleRequestMiddleware(c *gin.Context) {
	// 尝试将对象发送到通道，如果通道已满，表示有正在处理的请求，直接丢弃当前请求
	select {
	case s.ch <- struct{}{}:
		defer func() {
			// 处理完请求后，从通道中取出对象，释放通道，允许下一个请求继续处理
			<-s.ch
		}()
	default:
		// 如果通道已满，表示有正在处理的请求，直接返回
		CxtResponse(c, "sync")
		c.Abort() // 终止请求处理
		return
	}

	c.Next()
}

func CxtResponse(cxt *gin.Context, str string) {
	cxt.JSON(200, gin.H{
		"code":    1,
		"message": str,
	})
}

func CxtSuccessResponse(cxt *gin.Context) {
	cxt.JSON(200, gin.H{
		"code":    0,
		"message": "success",
	})
}

func CxtSuccessResponseData(cxt *gin.Context, data interface{}) {
	cxt.JSON(200, gin.H{
		"code":    0,
		"message": "success",
		"data":    data,
	})
}
