# 价格预测（XGBoost）后端方案草案

数据源：`C:\Users\konng\Desktop\Agriculture-System\tb_price_train.sql`

## 1. 接口设计
- `GET /api/price/forecast?commodity=苹果&horizon=7`
  - 返回：`{ series: [{date,pred,lower,upper}], model: "xgboost", mape, updatedAt }`
  - horizon 默认 7（天），支持 3/7/14。

## 2. 数据准备
1) 导入 SQL  
```bash
mysql -u... -p... db_name < tb_price_train.sql
```
2) 假设表字段：`date`(日期), `commodity`(品类), `price`(数字), 可选 `origin`/`volume`。若字段不同，在特征构建前做 rename。

## 3. 特征工程
- 时间衍生：`dayofweek, month, weekofyear, is_weekend`
- 滞后：`lag1, lag3, lag7, lag14, lag28`
- 滑动统计：`ma7, ma14, ma28, std7`
- 节假日/季节：`is_holiday, season`
- 可选外生：成交量、产地天气、库存等
- 处理缺失：前向填充 + 均值/中位数补齐；异常值用 IQR/3σ 裁剪。

## 4. 训练流程（按品类分模型）
伪代码（Python）：
```python
import pandas as pd, xgboost as xgb
from sklearn.model_selection import TimeSeriesSplit
from sklearn.metrics import mean_absolute_percentage_error

df = load_sql("tb_price_train.sql")  # 自行实现
df["date"] = pd.to_datetime(df["date"])
df = df.sort_values(["commodity", "date"])

def build_feats(sub):
    sub = sub.copy()
    for lag in [1,3,7,14,28]:
        sub[f"lag{lag}"] = sub.price.shift(lag)
    for win in [7,14,28]:
        sub[f"ma{win}"] = sub.price.rolling(win).mean()
    sub["dow"] = sub.date.dt.dayofweek
    sub["month"] = sub.date.dt.month
    sub = sub.dropna()
    return sub

models = {}
for name, sub in df.groupby("commodity"):
    sub = build_feats(sub)
    X, y = sub.drop(columns=["price", "date", "commodity"]), sub.price
    tscv = TimeSeriesSplit(n_splits=4)
    best = None
    for tr, va in tscv.split(X):
        model = xgb.XGBRegressor(
            n_estimators=500, max_depth=6, learning_rate=0.05,
            subsample=0.8, colsample_bytree=0.8, objective="reg:squarederror"
        )
        model.fit(X.iloc[tr], y.iloc[tr])
        pred = model.predict(X.iloc[va])
        mape = mean_absolute_percentage_error(y.iloc[va], pred)
        best = (mape, model) if best is None or mape < best[0] else best
    models[name] = best[1]
    best[1].save_model(f"model_{name}.json")
```

## 5. 推理（多步滚动）
1) 取最近 28 天数据构造最新特征。  
2) 逐步预测 horizon 天：每预测一天，把该预测值作为 lag1/lag3/... 递推生成下一天特征。  
3) 输出均值/上下界：上下界可用训练残差的标准差 `sigma`，返回 `pred±1.64*sigma` 作为 90% 区间。  

## 6. 服务化
- 启动时加载每个品类的模型文件到内存。
- 预测接口流程：
  1. 校验参数，缺省 horizon=7。
  2. 查询最近 60 天该品类价格；若不足用全局均值补齐并提示。
  3. 调用滚动预测生成 `series`。
  4. 返回 `series + mape(或最近交叉验证得分) + updatedAt`。
- 缓存：同一品类结果缓存 30-60 分钟（Redis/内存）。

## 7. 训练调度
- 定时任务（每天/每周）拉取最新行情 → 重训 → 覆盖模型文件。
- 记录每次训练的 MAPE/RMSE 与时间戳，便于监控。

## 8. 最佳实践
- 数据集极小时：减少滞后阶数，增大正则（`reg_alpha/reg_lambda`），避免过拟合。
- 多品类少样本：可共享一个模型并加入 `commodity` one-hot 作为特征；若差异大仍建议分模型。
- 监控：接口返回的 `updatedAt` 和 `mape` 同步到前端，误差异常时触发告警。

## 9. 目录建议
- `backend/price/forecast_train.py` 训练脚本
- `backend/price/model_store/` 模型与特征规范
- `backend/price/router.py` 定义 `/api/price/forecast`
- `backend/price/service.py` 读取最新数据 + 预测逻辑

这样即可与前端的首页/货源预测卡片对接。

