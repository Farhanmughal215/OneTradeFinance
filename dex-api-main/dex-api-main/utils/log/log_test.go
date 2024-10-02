package log

import (
	"go.uber.org/zap"
	"testing"
)

func Test_Zap(t *testing.T) {
	Init("test")
	for i := 0; i < 1; i++ {
		Info("test string")
		Warn("test int", zap.Int("data", 1))
		Error("test struct data", zap.String("data", ToJSON(t)))
	}
}
