MERGE INTO sys_admin KEY (username)
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800138000', 'admin@caiwu.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_user KEY (username)
VALUES ('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', 1, '13800138001', 'zhangsan@example.com', NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_user KEY (username)
VALUES ('lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', 2, '13800138002', 'lisi@example.com', NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_user KEY (username)
VALUES ('wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', 1, '13800138003', 'wangwu@example.com', NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_user KEY (username)
VALUES ('zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '赵六', 1, '13800138004', 'zhaoliu@example.com', NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_user KEY (username)
VALUES ('qianqi', 'e10adc3949ba59abbe56e057f20f883e', '钱七', 2, '13800138005', 'qianqi@example.com', NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('工资', 1, NULL, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('奖金', 1, NULL, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('投资收益', 1, NULL, 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('兼职收入', 1, NULL, 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('其他收入', 1, NULL, 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('餐饮', 2, NULL, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('交通', 2, NULL, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('购物', 2, NULL, 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('娱乐', 2, NULL, 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('医疗', 2, NULL, 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('教育', 2, NULL, 6, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('住房', 2, NULL, 7, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_category KEY (name, type)
VALUES ('其他支出', 2, NULL, 8, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_news KEY (title)
VALUES ('2024年一季度GDP同比增长5.2%', '国家统计局发布数据，2024年一季度国内生产总值按不变价格计算，同比增长5.2%。',
'国家统计局4月17日发布数据，初步核算，2024年一季度国内生产总值（GDP）按不变价格计算，同比增长5.2%，比上年全年加快2.1个百分点。
分产业看，第一产业增加值同比增长3.5%；第二产业增加值增长6.0%；第三产业增加值增长4.9%。
从环比看，一季度GDP比2023年四季度增长1.2%。', NULL, '财经记者', 1250, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_news KEY (title)
VALUES ('央行宣布降准0.5个百分点', '中国人民银行决定下调金融机构存款准备金率，释放长期资金约1万亿元。',
'中国人民银行决定于2024年4月25日下调金融机构存款准备金率0.5个百分点（不含已执行5%存款准备金率的金融机构）。
本次下调后，金融机构加权平均存款准备金率约为7.1%。
央行有关负责人表示，此次降准旨在保持银行体系流动性合理充裕，增强金融机构资金配置能力，加大对实体经济的支持力度。', NULL, '央行', 2180, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_news KEY (title)
VALUES ('个人所得税起征点或将提高', '财政部正在研究个人所得税改革方案，起征点有望进一步提高。',
'财政部相关负责人近日表示，正在研究进一步完善个人所得税制度的方案，其中包括提高个人所得税起征点。
专家分析，提高个税起征点将有助于减轻中低收入群体负担，促进消费增长。
目前我国个人所得税起征点为每月5000元。', NULL, '财经评论员', 890, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_news KEY (title)
VALUES ('股市震荡调整，专家建议谨慎投资', '近期A股市场出现较大波动，专家建议投资者保持理性，谨慎操作。',
'近期A股市场出现明显震荡调整，主要指数均有不同程度下跌。
市场分析人士认为，此次调整主要受多重因素影响：一是外围市场波动传导；二是部分投资者获利了结；三是市场情绪面偏谨慎。
专家建议投资者应保持理性，关注基本面优良的个股，避免盲目追涨杀跌。', NULL, '证券分析师', 3200, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

MERGE INTO sys_news KEY (title)
VALUES ('房地产市场回暖迹象明显', '多地房地产市场出现回暖迹象，成交量环比上升。',
'根据最新数据显示，全国多个城市房地产市场出现回暖迹象。
一线城市中，北京、上海、深圳的新房成交量环比均有不同程度上涨；
二线城市中，杭州、南京、成都等城市成交量回升明显。
业内人士分析，近期一系列房地产支持政策的出台是市场回暖的重要原因。', NULL, '房产周刊', 1560, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
