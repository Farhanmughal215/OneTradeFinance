package service

import (
	"DexAPI/config"
	"DexAPI/utils/log"
	"context"
	"crypto/ecdsa"
	"errors"
	"github.com/ethereum/go-ethereum/common"
	"github.com/ethereum/go-ethereum/crypto"
	"github.com/ethereum/go-ethereum/ethclient"
	"github.com/gin-gonic/gin"
	"github.com/juju/ratelimit"
	"go.uber.org/zap"
	"net/http"
	"time"
)

type Service struct {
	webService http.Server
	g          *gin.Engine
	buckets    *ratelimit.Bucket

	ethClient *ethclient.Client

	contractAddress common.Address
	fromAddress     common.Address
	privateKey      *ecdsa.PrivateKey

	ch chan struct{}
}

func NewService() (*Service, error) {
	log.Info("NewService",
		zap.String("listenerPort", config.Conf.Svc.ListenerPort),
		zap.String("dev", config.Conf.Svc.Env))

	svr := &Service{}

	svr.ch = make(chan struct{}, 1)

	svr.buckets = ratelimit.NewBucketWithQuantum(time.Second, 20, 20)
	svr.g = svr.newGin()
	svr.webService = http.Server{
		Addr:    config.Conf.Svc.ListenerPort,
		Handler: svr.g,
	}

	client, err := ethclient.Dial(config.Conf.Svc.Url)
	if err != nil {
		return nil, err
	}

	privateKey, err := crypto.HexToECDSA(config.Conf.Svc.PrivateKey[2:])
	if err != nil {
		return nil, err
	}

	fromAddr, err := toFromAddr(privateKey)

	svr.ethClient = client
	svr.contractAddress = common.HexToAddress(config.Conf.Svc.ContractAddress)
	svr.privateKey = privateKey
	svr.fromAddress = fromAddr

	return svr, nil
}

func (s *Service) newGin() *gin.Engine {
	gin.SetMode(gin.ReleaseMode)
	gin.DisableConsoleColor()
	ginRouter := gin.New()
	//ginRouter.Use(gin.Recovery(), cors(), s.bucket(), s.singleRequestMiddleware, s.panicMiddleware())
	ginRouter.Use(gin.Recovery(), cors(), s.panicMiddleware()) //取消限制

	ginRouter.POST("/transferOutToken", s.DexTransferOutTokenService)
	ginRouter.POST("/getNativeBalance", s.GetNativeBalance)

	ginRouter.POST("/ping", func(c *gin.Context) { c.JSON(200, nil) })
	ginRouter.GET("/ping", func(c *gin.Context) { c.JSON(200, nil) })

	return ginRouter
}

func (s *Service) Run() {
	if err := s.webService.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
		log.Error("web.run", zap.String("data", err.Error()))
	}
}

func (s *Service) Stop() {
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err := s.webService.Shutdown(ctx); err != nil {
		log.Error("web.stop", zap.String("data", err.Error()))
	}
}

func toFromAddr(privateKey *ecdsa.PrivateKey) (common.Address, error) {
	// 出账的公钥
	publicKey := privateKey.Public()
	publicKeyECDSA, ok := publicKey.(*ecdsa.PublicKey)
	if !ok {
		return common.Address{}, errors.New("cannot assert type: publicKey is not of type *ecdsa.PublicKey")
	}

	addr := crypto.PubkeyToAddress(*publicKeyECDSA)

	// 进账的地址。
	return addr, nil
}
