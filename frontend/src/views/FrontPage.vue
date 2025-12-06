<template>
  <div class="front-page-container">
    <!-- 主内容区 -->
    <div class="front-page-main">
      <!-- 轮播 + 公告 -->
      <section class="hero">
        <div class="carousel">
          <el-carousel :interval="4000" height="400px" indicator-position="outside">
            <el-carousel-item v-for="(img, idx) in imageList" :key="idx">
              <div class="carousel-item">
                <img :src="img.src" :alt="img.name" @error="e => e.target.src = require('../assets/img/placeholder.jpg')" />
          </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <div class="hero-right">
          <div class="headline-header">
            <span class="tab active">近日要闻</span>
          </div>
          <ul class="headline-list">
            <li v-for="(item, idx) in headlines" :key="idx">
              <span class="headline-text">{{ item.title }}</span>
              <span class="headline-date">{{ item.date }}</span>
            </li>
          </ul>
        </div>
      </section>

      <!-- 价格预测 CTA -->
      <section class="forecast-cta card">
        <div class="cta-left">
          <div class="tag">价格预测</div>
          <h3>主要品类未来7天价格走势</h3>
          <p class="desc">基于 XGBoost 时序回归，结合滞后与季节特征，帮助您提前锁定采购成本。</p>
          <div class="cta-metrics">
            <div class="metric">
              <span class="label">预测均价</span>
              <span class="value">{{ homeForecast.avg }} 元/斤</span>
            </div>
            <div class="metric">
              <span class="label">波动范围</span>
              <span class="value">{{ homeForecast.range }}</span>
            </div>
            <div class="metric">
              <span class="label">模型</span>
              <span class="value">XGBoost</span>
            </div>
          </div>
          <el-button type="primary" size="medium" @click="goForecast">
            查看货源预测
          </el-button>
        </div>
        <div class="cta-right">
          <div class="mini-chart" v-if="homeForecast.series.length">
            <div
              v-for="(p, idx) in homeForecast.series"
              :key="idx"
              class="mini-bar"
              :style="{ height: p.barHeight + '%'}"
            >
              <span class="mini-value">{{ p.pred }}</span>
              <span class="mini-date">{{ p.date }}</span>
            </div>
          </div>
          <div v-else class="mini-skeleton">
            <el-skeleton animated :rows="2"></el-skeleton>
          </div>
        </div>
      </section>

      <!-- 农产品供应模块 -->
      <supply-section
        v-for="section in supplySections"
        :key="section.title"
        :title="section.title"
        :subtitle="section.subtitle"
        :themeColor="section.themeColor"
        :leftImage="section.leftImage"
        :leftBg="section.leftBg"
        :categories="section.categories"
        :featuredGoods="section.featuredGoods"
        :suppliers="section.suppliers"
      />

      <!-- 农业技术大百科 -->
      <section class="tech-encyclopedia card">
        <div class="tech-header">
          <h3>农业技术大百科</h3>
          <span class="more-link">更多>></span>
        </div>
        
        <div class="tech-nav">
          <span class="nav-item active">栽培技术</span>
          <span class="nav-item">养殖技术</span>
          <span class="nav-item">病害防治</span>
          <span class="nav-item">植保技术</span>
          <span class="nav-item">加工保鲜</span>
          <span class="nav-item">捕捞技术</span>
          <span class="nav-item">节水灌溉</span>
          <span class="nav-item">育种技术</span>
        </div>
        
        <div class="tech-grid">
          <div class="tech-card" v-for="(tech, idx) in techCards" :key="idx">
            <div class="tech-image">
              <img :src="tech.image" :alt="tech.title" />
            </div>
            <div class="tech-content">
              <h4 class="tech-title">{{ tech.title }}</h4>
              <ul class="tech-topics">
                <li v-for="(topic, tIdx) in tech.topics" :key="tIdx">{{ topic }}</li>
              </ul>
            </div>
          </div>
        </div>
      </section>

      <!-- 专家专区 -->
      <section class="expert-zone card">
        <div class="expert-columns">
          <!-- 热门专家 -->
          <div class="popular-experts">
            <div class="section-header">
              <h3>热门专家</h3>
              <span class="more-link">更多></span>
            </div>
        <div class="expert-list">
              <div class="expert-card" v-for="(expert, idx) in popularExperts" :key="idx">
                <div class="expert-avatar">
                  <img :src="expert.avatar" :alt="expert.name" />
                </div>
            <div class="expert-info">
                  <div class="expert-name">{{ expert.name }}</div>
                  <div class="expert-title">{{ expert.title }}</div>
                  <div class="expert-affiliation">{{ expert.affiliation }}</div>
                  <div class="expert-specialties">{{ expert.specialties }}</div>
                  <div class="expert-stats">
                    <span class="stat-item">
                    <span class="stat-label">咨询数</span>
                      <span class="stat-value">{{ expert.consultations }}</span>
                    </span>
                    <span class="stat-item">
                    <span class="stat-label">回复数</span>
                      <span class="stat-value">{{ expert.replies }}</span>
                    </span>
                    <span class="stat-item">
                    <span class="stat-label">回复率</span>
                      <span class="stat-value">{{ expert.replyRate }}</span>
                    </span>
                  </div>
                  <button class="ask-btn">限时免费提问</button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 专家回答 -->
          <div class="expert-answers">
            <div class="section-header">
              <h3>专家回答</h3>
              <span class="more-link">更多></span>
            </div>
            <div class="qa-list">
              <div class="qa-item" v-for="(qa, idx) in expertQAs" :key="idx">
                <div class="question">
                  <span class="qa-icon question-icon">问</span>
                  <span class="qa-text">{{ qa.question }}</span>
                </div>
                <div class="answer">
                  <span class="qa-icon answer-icon">答</span>
                  <span class="qa-text">{{ qa.answer }}</span>
                </div>
                <div class="qa-meta">
                  <span class="answer-count">全部{{ qa.answerCount }}个回答</span>
                  <span class="qa-date">{{ qa.date }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

    </div>

  </div>
</template>

<script>
import SupplySection from '@/components/SupplySection.vue'
import { selectAllPage, selectGoodsPage } from '@/api/order'
import { getPriceForecast } from '@/api/price'

export default {
  name: 'FrontPage',
  components: { SupplySection },
  data() {
    return {
      goods: [],
      goodsTopics: [],
      carouselImg: true,
      homeForecast: {
        avg: '--',
        range: '--',
        series: []
      },
      supplySections: [
        {
          title: '农资农机',
          subtitle: '产地直发 海量供应',
          themeColor: '#39b54a',
          leftImage: require('@/assets/img/pro1.jpg'),
          leftBg: '',
          categories: [
            { title: '农用机械设备', items: ['农用拖拉机', '农机配件', '耕整机', '食品加工机械', '果蔬加工机', '植保无人机', '饲料制粒线', '肥料包装机', '发电机组'] },
            { title: '农药', items: ['杀虫剂', '杀菌剂', '杀螨剂', '除草剂', '生物农药', '植物调节剂', '助剂', '农药套餐', '拌种剂', '驱避剂'] },
            { title: '肥料', items: ['复合肥', '缓释肥', '磷肥', '单质肥', '生物肥', '水溶肥', '叶面肥', '有机肥', '控释肥', '着色剂'] },
            { title: '饲料', items: ['青绿饲料', '预混料', '宠物饲料', '饼粕饲料', '蛋白饲料', '干草饲料', '谷物饲料', '秸秆饲料', '糖麸饲料'] },
            { title: '排灌设备', items: ['涵管', '管材管件', '滴灌设备', '过滤设备', '灌溉控制器', '喷灌机', '温室骨架', '排水泵', '蓄水池'] }
          ],
          featuredGoods: [
            { img: require('@/assets/img/pro2.jpg'), price: '0.60', unit: '元/亩', title: '吡虫啉10%可湿性粉剂', tags: ['假货必赔', '破损补发'] },
            { img: require('@/assets/img/pro3.jpg'), price: '40.00', unit: '元/斤', title: '亮装40%苯甲·吡虫啉', tags: ['假货必赔', '破损补发'] }
          ],
          suppliers: ['粮仓满农业', '唐山鸿威农业科技有限公司', '麦多农资', '新农堂温室大棚', '鑫立发农资化工', '乡吧佬特产店', '望卡水溶肥工厂店', '魔枝草业', '诚信禽畜养殖', '益博农资批发']
        },
        {
          title: '热门供应',
          subtitle: '蔬菜',
          themeColor: '#2ca02c',
          leftImage: require('@/assets/img/fruit.png'),
          leftBg: 'linear-gradient(90deg,#e6f7e9,#ffffff)',
          categories: [
            { title: '葱姜蒜类', items: ['韭黄', '蒜苗', '大蒜', '生姜', '大葱', '小葱', '蒜苔', '洋葱', '蕹菜', '洋姜'] },
            { title: '根茎菜类', items: ['鲜百合', '芋头', '萝卜', '胡萝卜', '竹笋', '葛根', '魔芋', '木薯', '凉薯', '山药'] },
            { title: '叶菜类', items: ['白菜', '小白菜', '生菜', '苋菜', '娃娃菜', '木耳菜', '香菜', '油麦菜', '空心菜', '菠菜'] },
            { title: '豆菜类', items: ['毛豆', '刀豆', '四季豆', '豆角', '扁豆', '豇豆', '荷兰豆', '四棱豆', '白玉豆', '虎爪豆'] },
            { title: '食用菌', items: ['松杉灵芝', '元蘑', '珊瑚菌', '鹿茸菇', '栗蘑', '猴头菇', '荷叶菇', '海鲜菇', '毛尖菇', '姬松茸'] }
          ],
          featuredGoods: [
            { img: require('@/assets/img/pro1.jpg'), price: '4.50', unit: '元/斤', title: '“振杞”牌大蒜', tags: ['不对版包退', '坏损包赔'] },
            { img: require('@/assets/img/pro2.jpg'), price: '39.00', unit: '元/斤', title: '厚肉冬花菇干货', tags: ['不对版包退', '携保包赔'] }
          ],
          suppliers: ['河北腾达商贸', '小郭蔬菜供应链', '云南誉蔬菜代采', '菇诚贝用菌菇农场', '聚成小镇食品调料', '新发地蔬菜批发', '马鲜牛果蔬直发', '怀德地产菜场', '廉菇种植基地', '小陈果蔬代购']
        },
        {
          title: '粮油米面',
          subtitle: '产地直发 海量供应',
          themeColor: '#ffa726',
          leftImage: require('@/assets/img/rice.png'),
          leftBg: 'linear-gradient(90deg,#fff4e6,#ffffff)',
          categories: [
            { title: '食用油', items: ['松子油', '美藤果油', '牛油果油', '山茶油', '花生油', '橄榄油', '玉米油', '菜籽油', '葵花籽油', '色拉油'] },
            { title: '调味品', items: ['菌油', '味精', '鸡精', '十三香', '胡椒粉', '酱油', '蚝油', '调味酱', '胡椒粒', '调味粉'] },
            { title: '谷物粉淀', items: ['淀粉', '小麦粉', '粘米粉', '黄豆粉', '燕麦粉', '马蹄粉', '高粱面粉', '玉米糁', '豆面', '红豆粉'] },
            { title: '面食米食', items: ['饼干', '凉糕', '肠粉', '锅贴', '西米', '馒头', '粉干', '面条', '水饺', '汤圆'] }
          ],
          featuredGoods: [
            { img: require('@/assets/img/pro2.jpg'), price: '35.00', unit: '元/袋', title: '五常大米长粒香米', tags: ['不对版包退', '坏损包赔'] },
            { img: require('@/assets/img/pro3.jpg'), price: '60.00', unit: '元/袋', title: '五常稻花香二号', tags: ['不对版包退', '坏损包赔'] }
          ],
          suppliers: ['糖心蜜蜜农业', '小宋水产粮食', '廖竹粮油行', '周口一粮仓食品', '罗杜食品店', '昌薄荞电子商务', '金小甜茶树荷', '豪豆制品加工', '赛园芸港供应链', '番茄酱供应商']
        },
        {
          title: '水产',
          subtitle: '产地直发 海量供应',
          themeColor: '#42a5f5',
          leftImage: require('@/assets/img/pro1.jpg'),
          leftBg: 'linear-gradient(90deg,#e7f3ff,#ffffff)',
          categories: [
            { title: '水产种苗', items: ['鱼苗', '甲鱼苗', '虾苗', '蟹苗', '蛙苗', '蛏苗', '龟苗', '水蛭苗', '螺苗', '海参苗'] },
            { title: '贝类', items: ['生蚝', '珍珠贝', '鲍鱼', '鲜扇贝', '蛤蜊', '淡水蚌', '蜗牛', '虾夷贝', '青口', '海鲜礼盒'] },
            { title: '蟹类', items: ['大闸蟹', '梭子蟹', '青蟹', '招潮蟹', '石蟹', '珍宝蟹', '长脚蟹', '岩蟹', '雪蟹', '香辣蟹'] },
            { title: '食用鱼类', items: ['鲫鱼', '鲤鱼', '泥鳅', '银鱼', '草鱼', '黄鳝', '青鱼', '鲢鱼', '鳙鱼', '鲶鱼'] },
            { title: '软体动物', items: ['墨鱼', '鱿鱼', '海蜇', '海参', '泥丁', '泥螺', '沙虫', '章鱼', '海兔', '海胆'] }
          ],
          featuredGoods: [
            { img: require('@/assets/img/pro2.jpg'), price: '2.00', unit: '元/只', title: '生蚝鲜（海头直发）', tags: ['基地直发', '不对版包退'] },
            { img: require('@/assets/img/pro3.jpg'), price: '12.00', unit: '元/斤', title: '湖北小龙虾产地直供', tags: ['死包赔', '规格保障'] }
          ],
          suppliers: ['郝佳农场', '阿成水产', '臻品堂农产品', '南北生鲜', '诚君农贸', '鸿福水产养殖合作社', '鱼虾批发', '滇州丰丰水产', '浩海海产批发', '贝特鲜生虾工厂']
        },
        {
          title: '水果',
          subtitle: '产地直发 海量供应',
          themeColor: '#ff7043',
          leftImage: require('@/assets/img/fruit.png'),
          leftBg: 'linear-gradient(90deg,#ffeae3,#ffffff)',
          categories: [
            { title: '热带水果', items: ['芒果', '面包果', '荔枝', '龙眼', '菠萝', '火龙果', '香蕉', '榴莲', '木瓜', '莲雾'] },
            { title: '柑橘类', items: ['柠檬', '柑桔', '金桔', '橙子', '柚子'] },
            { title: '浆果类', items: ['红醋栗', '黑加仑', '葡萄', '草莓', '树莓', '圣女果', '桑葚', '蓝莓', '覆盆子', '小番茄'] },
            { title: '瓜果类', items: ['西瓜', '甜瓜', '哈密瓜', '香瓜', '南汇密瓜', '麒麟瓜', '羊角蜜', '蒲瓜', '黄瓜', '冬瓜'] },
            { title: '核果仁果', items: ['苹果', '梨', '山楂', '沙果', '李子', '鲜枣', '杏', '杨梅', '青梅', '蜜桃'] }
          ],
          featuredGoods: [
            { img: require('@/assets/img/pro1.jpg'), price: '1.90', unit: '元/斤', title: '软蜜高甜黄金百香果', tags: ['不对版包退', '坏损包赔'] },
            { img: require('@/assets/img/pro2.jpg'), price: '3.95', unit: '元/斤', title: '赣南椪柑会员甜桔', tags: ['不对版包退', '坏损包赔'] }
          ],
          suppliers: ['孙哥甜瓜西红代收', '草具小鹰瓜果合作社', '兴谷农业合作社', '大姜安波瑞丰果业', '小刘农贸果铺', '立贝果蔬批发', '山东老家农生鲜', '大槐皇种植合作社', '福润果业', '辽宁福哥果业']
        },
        {
          title: '畜禽肉蛋',
          subtitle: '产地直发 海量供应',
          themeColor: '#f2b77f',
          leftImage: require('@/assets/img/person.png'),
          leftBg: 'linear-gradient(90deg,#fff3e5,#ffffff)',
          categories: [
            { title: '禽畜', items: ['鸡苗', '鸭苗', '鹅苗', '鸽苗', '鸵鸟苗', '野鸡苗', '鹧鸪苗', '鸸鹋苗', '野猪苗'] },
            { title: '肉类', items: ['特种肉', '鸡副产品', '鸭副产品', '鹅副产品', '猪副产品', '牛肉', '羊副产品'] },
            { title: '蛋类', items: ['鸡蛋', '鸭蛋', '鹅蛋', '鸽子蛋', '皮蛋', '咸蛋', '毛蛋', '特种蛋', '甲鱼蛋'] },
            { title: '活畜', items: ['黄牛', '黑山羊', '生猪', '梅花鹿', '肉驴', '肉牛', '骆驼', '兔子', '野兔', '香猪'] }
          ],
          featuredGoods: [
            { img: require('@/assets/img/pro3.jpg'), price: '98.00', unit: '元/只', title: '无抗林下散养土鸡', tags: ['不对版包退', '坏损包赔'] },
            { img: require('@/assets/img/pro1.jpg'), price: '0.80', unit: '元/枚', title: '土鸡蛋 湖湘土鸡蛋', tags: ['死包赔', '规格保障'] }
          ],
          suppliers: ['山东保山清真肉类', '楚畜食品', '农村二改型产业园', '吉林省世博栏业', '山东承梁畜禽批发', '鑫鑫畜禽销售', '鸿源清真肉类', '散养生态笨鸡场', '成良草牛养殖中心', '瑞康牛羊肉馆']
        }
      ],
      imageList: [
        { src: require('@/assets/img/farm.jpeg'), name: '农机作业' },
        { src: require('@/assets/img/pro1.jpg'), name: '农产品一' },
        { src: require('@/assets/img/pro2.jpg'), name: '农产品二' },
        { src: require('@/assets/img/pro3.jpg'), name: '农产品三' }
      ],
      headlines: (() => {
        const titles = [
          '韩长赋：确保水稻产量稳定在2亿吨以上',
          '黄浦江大闸蟹今开捕',
          '新疆农用地土壤环境总体优良',
          '河南秋收基本结束 麦播超四成',
          '河北沧州开展农产品质量安全普法宣传',
          '安徽农业产业化交易会举行',
          '禄丰县：做大特色产业助农增收',
          '黑龙江抢收保收 打赢秋收战役',
          '陇南市已建成高标准农田600多万亩',
          '柯城区全面启动养殖污染治理',
          '永安上坪乡：粮食喜获丰收'
        ]
        const formatDate = (daysAgo) => {
          const date = new Date()
          date.setDate(date.getDate() - daysAgo)
          const year = date.getFullYear()
          const month = String(date.getMonth() + 1).padStart(2, '0')
          const day = String(date.getDate()).padStart(2, '0')
          return `${year}-${month}-${day}`
        }
        return titles.map((title, index) => ({
          title,
          date: formatDate(index + 1)
        }))
      })(),
      techCards: [
        {
          title: '蔬菜技术',
          image: require('@/assets/img/pro1.jpg'),
          topics: [
            '反季节蔬菜施肥关键要点',
            '西葫芦结瓜期如何摘叶疏果',
            '甜瓜黄足黑守瓜防治方案',
            '蔬菜错用农药的补救办法',
            '阳台蔬菜水肥管理技巧',
            '黄瓜细菌性流胶病快速处置'
          ]
        },
        {
          title: '水果技术',
          image: require('@/assets/img/pro2.jpg'),
          topics: [
            '草莓着色不匀的原因分析',
            '板栗采果后及时施肥促复壮',
            '温室桃树采果后的管理要点',
            '草莓灰霉病防控技巧',
            '梨园套种需要注意什么',
            '草莓炭疽病识别与处理'
          ]
        },
        {
          title: '园林技术',
          image: require('@/assets/img/pro3.jpg'),
          topics: [
            '菩提树霉斑病与疥虫防治',
            '迷迭香的施肥方法',
            '黄花槐苗木繁殖方法',
            '红掌施肥注意事项',
            '桂花嫁接技术',
            '桂花施肥需要注意什么'
          ]
        },
        {
          title: '茶叶技术',
          image: require('@/assets/img/pro1.jpg'),
          topics: [
            '儿茶栽培技术',
            '绿色防治茶园叶枯病',
            '春茶种植防寒工作要点',
            '茶园田间管理五要素',
            '冲泡好茶的关键细节',
            '茶叶耐泡度与品种关系'
          ]
        },
        {
          title: '大田技术',
          image: require('@/assets/img/pro2.jpg'),
          topics: [
            '小麦防倒伏重在春管',
            '沙土种植水稻怎样才能高产',
            '水稻矮缩病的防治方法',
            '水稻胡麻斑病防治技术',
            '水稻扬花期病虫害防控',
            '水稻浮肥现象产生原因'
          ]
        },
        {
          title: '农化技术',
          image: require('@/assets/img/pro3.jpg'),
          topics: [
            '农作物叶面肥施肥特点',
            '冬春种菜中午喷药不宜',
            '农作物钾肥应用常识',
            '果树秋基肥选择技巧',
            '肥料运用的五大误区',
            '五类西瓜白粉病常用杀菌剂'
          ]
        },
        {
          title: '温室技术',
          image: require('@/assets/img/pro1.jpg'),
          topics: [
            '棚菜浇水灌溉五要点',
            '小麦怎样进行冬季灌溉',
            '温室蔬菜微肥使用技巧',
            '温室黄瓜巧防黑星病',
            '蔬菜受冻后该怎么补救',
            '工程节水技术'
          ]
        },
        {
          title: '水产技术',
          image: require('@/assets/img/pro2.jpg'),
          topics: [
            '浅海筏式鲍鱼养殖技术',
            '河蟹缺氧该怎么办',
            '冬天大棚养虾要点',
            '集约化养殖罗非鱼',
            '工厂化规模养殖甲鱼',
            '南美白对虾生态混养技术'
          ]
        },
        {
          title: '畜禽技术',
          image: require('@/assets/img/pro3.jpg'),
          topics: [
            '撒式发酵床养猪维护技术',
            '猪疥螨病怎么治做好这三点',
            '香猪养殖条件',
            '中小型养猪场常见问题',
            '猪饲料中能添加哪些中草药',
            '养猪效益低的常见原因'
          ]
        }
      ],
      popularExperts: [
        {
          name: '赵成林',
          title: '高级畜牧师',
          affiliation: '榆中县畜牧水产技术推广中心',
          specialties: '擅长牛、马、驴、骡、乌骨鸡、野猪、珍珠鸡等养殖与防疫',
          consultations: 40922,
          replies: 33770,
          replyRate: '82.52%',
          avatar: require('@/assets/img/person.png')
        },
        {
          name: '刚志远',
          title: '高级兽医师',
          affiliation: '方城县畜牧局',
          specialties: '擅长猪、牛、羊、鸡、鸭、鹅、兔等畜禽繁殖与疾病防控',
          consultations: 24425,
          replies: 26393,
          replyRate: '108.06%',
          avatar: require('@/assets/img/person.png')
        },
        {
          name: '戴届新',
          title: '高级畜牧兽医师',
          affiliation: '宁乡市农业农村局',
          specialties: '擅长畜禽、水产、蜜蜂等动物疫病防控',
          consultations: 19366,
          replies: 26351,
          replyRate: '136.07%',
          avatar: require('@/assets/img/person.png')
        }
      ],
      expertQAs: [
        {
          question: '请问老师,小龙虾塘需要定期改底吗?',
          answer: '小龙虾塘口需要定期改底, 可以每月进行一次底改并结合增氧。',
          answerCount: 1,
          date: '2025-10-22'
        },
        {
          question: '黄米(黍子)脱粒后里面混有很多秕粒怎么办?',
          answer: '准备一个孔径小于米粒大小的筛子,在筛...',
          answerCount: 1,
          date: '2025-10-22'
        },
        {
          question: '羊呼吸系统疾病如何预防?',
          answer: '加强饲养管理,注意圈舍防寒保暖,可以...',
          answerCount: 2,
          date: '2025-10-22'
        },
        {
          question: '水稻抽穗扬花温度要求?',
          answer: '水稻抽穗扬花期适宜温度在25-30摄氏度, 要保持田间通风。',
          answerCount: 1,
          date: '2025-10-22'
        }
      ],
      total: 0,
      pageSize: 30,
      searchValue: '',
      goodsCount2: sessionStorage.getItem('/order/All/pageCode') || 1
    }
  },
  mounted() {
    this.getData()
    this.getTopicData()
    this.loadHomeForecast()
  },
  methods: {
    async loadHomeForecast() {
      try {
        const res = await getPriceForecast({ commodity: '苹果', horizon: 7 })
        // 处理响应数据：后端返回格式为 { flag: true, data: { series, model, mape, updatedAt } }
        let payload = {};
        if (res.flag && res.data) {
          payload = res.data;
        } else if (res.series) {
          payload = res;
        } else if (res.data && res.data.series) {
          payload = res.data;
        } else {
          payload = res;
        }
        
        const series = payload.series || []
        if (!Array.isArray(series) || series.length === 0) {
          throw new Error('预测数据为空')
        }
        this.applyHomeSeries(series)
      } catch (e) {
        console.error('获取首页预测数据失败:', e)
        this.applyHomeSeries(this.getHomeSample())
      }
    },
    applyHomeSeries(series) {
      const normalized = series.map(item => ({
        date: item.date || item.ds || item.time || '',
        pred: Number(item.pred || item.yhat || item.value || item.price || 0).toFixed(2)
      }))
      const preds = normalized.map(i => Number(i.pred))
      const max = Math.max(...preds)
      const min = Math.min(...preds)
      const span = Math.max(max - min, 0.01)
      this.homeForecast.series = normalized.map(p => ({
        ...p,
        barHeight: ((Number(p.pred) - min) / span) * 70 + 20
      }))
      const avg = (preds.reduce((a, b) => a + b, 0) / preds.length).toFixed(2)
      this.homeForecast.avg = avg
      this.homeForecast.range = `${min.toFixed(2)} - ${max.toFixed(2)} 元/斤`
    },
    getHomeSample() {
      const today = new Date()
      return Array.from({ length: 6 }).map((_, idx) => {
        const d = new Date(today)
        d.setDate(d.getDate() + idx + 1)
        const price = 3.2 + Math.cos(idx / 2) * 0.15 + idx * 0.03
        return {
          date: `${d.getMonth() + 1}-${String(d.getDate()).padStart(2, '0')}`,
          pred: price.toFixed(2)
        }
      })
    },
    goForecast() {
      this.$router.push({ path: '/home/goods', query: { section: 'forecast', commodity: '苹果' } }).catch(() => {})
    },
    getData() {
      this.$store.commit('updateActiveIndex', '1')
      selectAllPage({
        keys: this.searchValue,
        pageNum: this.goodsCount2
      }).then(res => {
        if (res.flag) {
          this.goods = res.data.list
          this.total = res.data.total
        }
      })
    },
    getTopicData() {
      selectGoodsPage({ pageNum: '1', keys: '' }).then(res => {
        if (res.flag) this.goodsTopics = res.data.list.slice(0, 10)
      })
    },
    handleDetail(item) {
      this.$store.commit('updateOrderId', item.orderId)
      this.$router.push(`/home/details?orderId=${item.orderId}`).catch(() => {})
    },
    toPublish() {
      if (localStorage.getItem('token')) {
        this.$router.push('/home/addmessage/publishgoods').catch(() => {})
      } else {
        this.$router.push('/login').catch(() => {})
      }
    },
    loadMore() {
      console.log('加载更多')
    }
  }
}
</script>

<style lang="less" scoped>
/* 变量 */
@primary: #035D1C;
@grey: #f5f5f5;

.front-page-container {
  display: flex;
  width: 100%;
  margin-top: 10px;
  padding: 0 20px;
  @media (max-width: 768px) {
    flex-direction: column;
    width: 100%;
    padding: 10px;
  }
}

.front-page-main {
  width: 100%;
  @media (max-width: 768px) {
    width: 100%;
  }
}


/* 通用卡片 */
.card {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 轮播 */
.hero {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  @media (max-width: 500px) {
    flex-direction: column;
  }
}
.carousel {
  flex: 1;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  margin-top: 10px;
  
  /deep/ .el-carousel {
    border-radius: 8px;
    overflow: hidden;
  }
  
  /deep/ .el-carousel__container {
    border-radius: 8px;
    overflow: hidden;
  }
  
  /deep/ .el-carousel__item {
    border-radius: 8px;
    overflow: hidden;
  }
}
.carousel-item {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.forecast-cta {
  display: grid;
  grid-template-columns: 1.3fr 1fr;
  gap: 16px;
  align-items: center;

  @media (max-width: 960px) {
    grid-template-columns: 1fr;
  }

  .tag {
    display: inline-block;
    padding: 4px 8px;
    background: #ecf5ff;
    color: #409eff;
    border-radius: 4px;
    font-size: 12px;
    margin-bottom: 6px;
  }

  h3 {
    margin: 4px 0 6px;
    font-size: 22px;
    color: #2c3e50;
  }

  .desc {
    color: #606266;
    margin: 0 0 12px;
    line-height: 1.6;
  }

  .cta-metrics {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
    gap: 10px;
    margin: 12px 0 16px;

    .metric {
      background: #f7f9fc;
      padding: 10px 12px;
      border-radius: 8px;
      display: flex;
      flex-direction: column;
      gap: 6px;

      .label {
        font-size: 12px;
        color: #909399;
      }

      .value {
        font-size: 16px;
        color: #303133;
        font-weight: 600;
      }
    }
  }

  .cta-right {
    background: linear-gradient(180deg, #f9fbff 0%, #ffffff 100%);
    border: 1px solid #ebeef5;
    border-radius: 10px;
    padding: 14px;
  }

  .mini-chart {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(50px, 1fr));
    gap: 8px;
    align-items: end;
    min-height: 160px;
  }

  .mini-bar {
    background: #ecf5ff;
    border-radius: 6px 6px 2px 2px;
    position: relative;
    display: flex;
    align-items: flex-end;
    justify-content: center;
    transition: all 0.2s;

    &:hover {
      background: #d9ecff;
      transform: translateY(-3px);
    }

    .mini-value {
      position: absolute;
      top: -20px;
      font-size: 12px;
      color: #303133;
    }

    .mini-date {
      position: absolute;
      bottom: -18px;
      font-size: 12px;
      color: #909399;
    }
  }

  .mini-skeleton {
    padding: 10px;
  }
}
.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保持比例裁剪，适配容器 */
  border-radius: 8px;
}
.caption {
    position: absolute;
  left: 10px;
  bottom: 10px;
  padding: 4px 8px;
  font-size: 12px;
      color: #fff;
  background: rgba(0,0,0,0.45);
      border-radius: 4px;
}
.topic-thumb{ width:36px; height:36px; object-fit:cover; vertical-align:middle; margin-right:8px; border-radius:4px }
.carousel img{ max-height:360px }
.hero-right {
  flex: 1;
  min-width: 0;
  @media (max-width: 768px) {
    width: 100%;
  }
  .headline-header {
    display: flex;
    height: 36px;
    align-items: center;
    background: #e0f6da;
    border: 1px solid #c6e9bf;
    border-bottom: none;
    border-radius: 4px 4px 0 0;
    overflow: hidden;
    .tab {
      flex: 1;
      text-align: center;
      font-size: 14px;
      color: #666;
      background: #f2f2f2;
      padding: 8px 0;
    }
    .tab.active {
      background: #39b54a;
      color: #fff;
      font-weight: 600;
    }
  }
  .headline-list {
    list-style: none;
    padding: 8px 10px;
    margin: 0;
    border: 1px solid #e6e6e6;
    border-top: none;
    line-height: 22px;
    li {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 13px;
      padding: 4px 0;
      color: #333;
      cursor: pointer;
      &:hover { 
        .headline-text { color: @primary; }
      }
      .headline-text {
        flex: 1;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
      }
      .headline-date {
        flex-shrink: 0;
        margin-left: 10px;
        font-size: 12px;
        color: #999;
      }
    }
  }
}

/* 表格 */
.product-table {
  overflow-x: auto;
  table {
    width: 100%;
    border-collapse: collapse;
    font-size: 14px;
    th,
    td {
      padding: 10px 12px;
      border: 1px solid #eee;
    }
    th {
      background: @grey;
    }
    .empty {
      text-align: center;
      color: #999;
    }
  }
}

/* 农业技术大百科 */
.tech-encyclopedia {
  .tech-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    h3 {
      margin: 0;
      font-size: 18px;
      color: @primary;
    }
    .more-link {
      color: @primary;
      cursor: pointer;
      font-size: 14px;
      &:hover {
        text-decoration: underline;
      }
    }
  }
  
  .tech-nav {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
    .nav-item {
      padding: 8px 16px;
      cursor: pointer;
      border-radius: 4px;
      font-size: 14px;
      color: #666;
      transition: all 0.3s;
      &:hover {
        background: #f0f0f0;
        color: @primary;
      }
      &.active {
        background: @primary;
        color: #fff;
      }
    }
  }
  
  .tech-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    @media (max-width: 768px) {
      grid-template-columns: repeat(2, 1fr);
    }
    @media (max-width: 480px) {
      grid-template-columns: 1fr;
    }
  }
  
  .tech-card {
    display: flex;
    background: #fafafa;
    border-radius: 8px;
    overflow: hidden;
    transition: transform 0.3s, box-shadow 0.3s;
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    .tech-image {
      width: 80px;
      height: 80px;
      flex-shrink: 0;
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
    
    .tech-content {
      flex: 1;
      padding: 12px;
      .tech-title {
        margin: 0 0 8px 0;
        font-size: 14px;
        font-weight: 600;
        color: #333;
    text-align: center;
      }
      
      .tech-topics {
        list-style: none;
        padding: 0;
        margin: 0;
        li {
          font-size: 12px;
          line-height: 18px;
          color: #666;
          margin-bottom: 2px;
          cursor: pointer;
          &:hover {
            color: @primary;
          }
          &:before {
            content: "·";
            color: @primary;
            margin-right: 4px;
          }
        }
      }
    }
  }
}

/* 专家专区 */
.expert-zone {
  .expert-columns {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 30px;
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 20px;
    }
  }
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    h3 {
      margin: 0;
      font-size: 18px;
      color: @primary;
      font-weight: 600;
    }
    .more-link {
      color: @primary;
    cursor: pointer;
    font-size: 14px;
    &:hover {
        text-decoration: underline;
    }
  }
}

  .popular-experts {
  .expert-list {
    display: flex;
      flex-direction: column;
      gap: 16px;
    }
    
    .expert-card {
      display: flex;
      background: #fafafa;
      border-radius: 8px;
      padding: 16px;
      transition: transform 0.3s, box-shadow 0.3s;
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
      
      .expert-avatar {
        width: 60px;
        height: 60px;
        flex-shrink: 0;
        margin-right: 12px;
        img {
          width: 100%;
          height: 100%;
      border-radius: 50%;
          object-fit: cover;
        }
      }
      
      .expert-info {
    flex: 1;
        .expert-name {
          font-size: 16px;
          font-weight: 600;
          color: #333;
          margin-bottom: 4px;
        }
        .expert-title {
          font-size: 14px;
          color: @primary;
          margin-bottom: 4px;
        }
        .expert-affiliation {
          font-size: 12px;
          color: #666;
          margin-bottom: 6px;
        }
        .expert-specialties {
          font-size: 12px;
          color: #666;
          line-height: 16px;
          margin-bottom: 8px;
        }
        .expert-stats {
    display: flex;
    gap: 12px;
          margin-bottom: 8px;
          .stat-item {
            font-size: 12px;
            .stat-label {
              color: #666;
              margin-right: 4px;
            }
            .stat-value {
              color: @primary;
        font-weight: 600;
      }
          }
        }
        .ask-btn {
          background: @primary;
          color: #fff;
          border: none;
          padding: 6px 12px;
          border-radius: 4px;
        font-size: 12px;
          cursor: pointer;
          &:hover {
            background: darken(@primary, 10%);
      }
      }
    }
  }
}

  .expert-answers {
    .qa-list {
  display: flex;
    flex-direction: column;
      gap: 16px;
    }
    
    .qa-item {
      background: #fafafa;
      border-radius: 8px;
      padding: 16px;
      transition: transform 0.3s, box-shadow 0.3s;
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
      
      .question, .answer {
        display: flex;
        align-items: flex-start;
        margin-bottom: 8px;
        .qa-icon {
          width: 20px;
          height: 20px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
        font-weight: 600;
          margin-right: 8px;
          flex-shrink: 0;
        }
        .question-icon {
          background: @primary;
          color: #fff;
        }
        .answer-icon {
          background: #ff7043;
          color: #fff;
        }
        .qa-text {
    flex: 1;
          font-size: 14px;
          line-height: 20px;
          color: #333;
        }
      }
      
      .qa-meta {
        display: flex;
        justify-content: space-between;
        font-size: 12px;
        color: #666;
        margin-top: 8px;
        .answer-count {
          color: @primary;
        }
      }
    }
  }
}

</style>