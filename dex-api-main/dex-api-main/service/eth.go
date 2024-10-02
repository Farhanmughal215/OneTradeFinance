package service

import (
	"DexAPI/eth"
	"DexAPI/utils/log"
	"github.com/ethereum/go-ethereum/common"
	"github.com/ethereum/go-ethereum/common/hexutil"
	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
	"math/big"
	"sync"
)

type transferOutTokenReq struct {
	ToAddress string `json:"toAddress"`
	Amount    int64  `json:"amount"`
}

func (s *Service) DexTransferOutTokenService(cxt *gin.Context) {
	req := &transferOutTokenReq{}
	if err := cxt.Bind(req); err != nil {
		log.Error("DexTransferOutTokenService", zap.String("Bind", err.Error()))
		CxtResponse(cxt, err.Error())
		return
	}

	log.Info("DexTransferOutTokenService", zap.String("toAddress", req.ToAddress), zap.Int64("amount", req.Amount))

	if !common.IsHexAddress(req.ToAddress) {
		CxtResponse(cxt, "build")
		return
	}

	toAddress := common.HexToAddress(req.ToAddress)

	hash, err := eth.DexTransferOutToken(s.ethClient, s.contractAddress, s.fromAddress, toAddress, s.privateKey, req.Amount)
	if err != nil {
		log.Error("DexTransferOutTokenService", zap.String("DexTransferOutToken", err.Error()))
		CxtResponse(cxt, err.Error())
		return
	}

	CxtSuccessResponseData(cxt, hash)
}

type getNativeBalanceReq struct {
	AddressList []string `json:"address"`
}

// 查询原生币余额
func (s *Service) GetNativeBalance(cxt *gin.Context) {
	req := &getNativeBalanceReq{}
	if err := cxt.Bind(req); err != nil {
		log.Error("GetNativeBalance", zap.String("Bind", err.Error()))
		CxtResponse(cxt, err.Error())
		return
	}
	addMap := make(map[string]string)
	//去重复
	for _, add := range req.AddressList {
		addMap[add] = add
	}
	var wg sync.WaitGroup
	result := make(map[string]string)
	for _, add := range addMap {
		wg.Add(1)
		go func(address string) {
			defer wg.Done()
			if !common.IsHexAddress(address) {
				CxtResponse(cxt, "is not address")
				return
			}

			//查询余额
			var balance string
			err := s.ethClient.Client().Call(&balance, "eth_getBalance", address, "latest")
			if err != nil {
				log.Error("GetNativeBalance address: %s err: %v", zap.String(address, err.Error()))
				return
			}
			//生成一个*big.Int类型的指针，为18
			decimals := new(big.Int)
			decimals.SetInt64(18)
			//将string 类型 balance 转为 *big.Int 类型
			bi := new(big.Int)
			//十六进制转10进制
			balance = hexutil.MustDecodeBig(balance).String()
			bi.SetString(balance, 10)

			amount := conversionWeiAmount(bi, decimals)
			result[address] = amount
		}(add)
	}
	wg.Wait()
	CxtSuccessResponseData(cxt, result)
}

// conversionWeiAmount 精度计算
func conversionWeiAmount(amount *big.Int, decimals *big.Int) string {
	// 获取小数位数
	decimalsInt := decimals.Int64()
	// 创建 big.Rat 类型来进行高精度计算
	amountRat := new(big.Rat)
	if amount != nil {
		amountRat = new(big.Rat).SetInt(amount)
	} else {
		return "0"
	}
	decimalsRat := new(big.Rat).SetInt(new(big.Int).Exp(big.NewInt(10), decimals, nil))
	// 计算 Wei 数量
	amountWeiRat := new(big.Rat).Quo(amountRat, decimalsRat)
	// 如果结果不等于零，则将结果转换为字符串并设置精度和舍入模式
	result := ""
	if amountWeiRat.Cmp(big.NewRat(0, 1)) != 0 {
		result = amountWeiRat.FloatString(int(decimalsInt))
	} else {
		result = "0"
	}
	return result
}
