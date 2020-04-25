package entity

import "time"

// 资源信息表
type Assets struct {
	Id string
	// 文件名
	Name string

	// 文件大小
	Size int64

	Type string

	// 秒速
	Describe string

	CreateAt time.Time
	UpdateAt time.Time
}
