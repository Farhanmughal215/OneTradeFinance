package config

import (
	"flag"
	"github.com/spf13/viper"
)

var (
	env = flag.String("env", "test", "  测试环境:test  生产环境:pord")
)

type (
	conf struct {
		Svc *service `yaml:"service"`
	}

	service struct {
		Env             string `yaml:"env"`
		ContractAddress string `yaml:"contractAddress"`
		PrivateKey      string `yaml:"privateKey"`
		Url             string `yaml:"url"`
		ListenerPort    string `yaml:"listenerPort"`
	}
)

var Conf *conf

func ConfInit() error {
	Conf = &conf{
		Svc: &service{},
	}

	viper.SetConfigType("yaml")
	viper.AddConfigPath("./config/" + *env)

	if err := unmarshal("service", Conf.Svc); err != nil {
		return err
	}

	return nil
}

func unmarshal(name string, table any) error {
	viper.SetConfigName(name)
	if err := viper.ReadInConfig(); err != nil {
		return err
	}

	return viper.Unmarshal(table)
}
