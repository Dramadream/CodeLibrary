1. AIDL中的注释不能有中文。
2. 其他AIDL定义的AIDL接口和实现Parcelable序列化的类必须import，即使在相同包结构下，其余的类型不需要import；
3. AIDL中，对于非基本数据类型，也不是String和CharSequence类型的，需要有方向指示，包括in、out和inout，in表示由客户端设置，out表示由服务端设置，inout是两者均可设置。

