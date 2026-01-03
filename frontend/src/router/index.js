import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'
import { isTokenValid, getRole, clear } from '../utils/tokenManager'

import Login from '../views/Login'
import Register from '../views/Register'
import Home from '../views/Home'
import HomeGoods from '../views/HomeGoods'
import HomePurchase from '../views/HomePurchase'
import HomeTrade from '../views/HomeTrade'
import HomeExpert from '../views/HomeExpert'
import HomeBank from '../views/HomeBank'
import HomeKnowledge from '../views/HomeKnowledge'
import KnowledgeDetail from '../views/KnowledgeDetail'
import HomeCollect from '../views/HomeCollect'
import ShopCart from '../views/ShopCart'
import Payment from '../views/Payment'
import Details from '../views/Details'
import purchaseDetails from '../views/purchaseDetails'
import User from '../views/User'
import UserInfo from '../views/UserInfo'
import UserFinance from '../views/UserFinance'
import PublishNav from '../components/PublishNav.vue'
import PublishGoods from '../views/PublishGoods'
import PublishNeeds from '../views/PublishNeeds'
import PublishKnowledges from '../views/PublishKnowledges'
import PublishedGoods from '../views/PublishedGoods'
import PublishedNeeds from '../views/PublishedNeeds'
import PublishedGoodsAdmin from '../views/PublishedGoodsAdmin'
import PublishedNeedsAdmin from '../views/PublishedNeedsAdmin'
import PublishedKnowledges from '../views/PublishedKnowledges'
import UserBuy from '../views/UserBuy'
import UserSell from '../views/UserSell'
import expertQuestion from '../views/expertQuestion'
import expertAppoint from '../views/expertAppoint'
import expertInfo from '../components/expertInfo.vue'
import UserManage from '../views/UserManage'
import FrontPage from '../views/FrontPage'
import HomeGuide from '../views/HomeGuide'
import guideDetail from '../views/guideDetail'
import AllExpert from '../views/AllExpert'
import question from '../views/Question'
import appointment from '../views/Appointment'
import SmartMatch from '../views/SmartMatch'
import SmartMatchPage from '../views/SmartMatchPage'
import FinancingDetails from '../views/FinancingDetails'
import LoanProductDetail from '../views/LoanProductDetail'
import LoanApply from '../views/LoanApply'
import LoanApplySuccess from '../views/LoanApplySuccess'
import Chat from '../views/Chat'
import userGood from '../views/goodsManager.vue'
import AboutUs from '../views/AboutUs'
import Message from '../views/message'
import forgetBox from '../views/forgetBox'
import OrderInfo from '../views/OrderInfo'
import OrderEvaluate from '../views/OrderEvaluate'
import PublishSupply from '../views/PublishSupply'
import EditProduct from '../views/EditProduct'
import PurchaseList from '../views/PurchaseList'
import PublishNeed from '../views/PublishNeed'
import ExpertGuide from '../views/ExpertGuide'
import ExpertKnowledgeList from '../views/ExpertKnowledgeList'
import AllQuestions from '../views/AllQuestions'
import OnlineQuestions from '../views/OnlineQuestions'
import MyQuestions from '../views/MyQuestions'
import QuestionDetail from '../views/QuestionDetail'
import AskQuestion from '../views/AskQuestion'
import ExpertDashboard from '../views/ExpertDashboard'
import LoanReviewList from '../views/LoanReviewList'
import MyKnowledge from '../views/MyKnowledge'


Vue.use(VueRouter)


// 修复ElementUI和vue-router�?3.0版本冲突问题
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    component: Home,
    children: [
      {
        path: '',
        redirect: 'front'
      },
      {
        path: 'front',
        component: FrontPage
      },
      {
        path: 'trade',
        component: HomeTrade,
        meta: { roles: ['farmer'] }
      },
      {
        path: 'expertWork',
        component: HomeExpert,
        meta: { roles: ['expert'] }
      },
      {
        path: 'bankWork',
        component: HomeBank,
        meta: { roles: ['bank'] }
      },
      {
        path: 'goods',
        component: HomeGoods
      },
      {
        path: 'publishSupply',
        component: PublishSupply
      },
      {
        path: 'purchase',
        component: HomePurchase
      },
      {
        path: 'purchaseList',
        component: PurchaseList
      },
      {
        path: 'publishNeed',
        component: PublishNeed
      },
      {
        path: 'knowledge',
        component: HomeKnowledge
      },
      {
        path: 'knowledge/:id',
        component: KnowledgeDetail
      },
      {
        // ·����������� :id
        path: 'knowledgeDetail/:id', 
        name: 'KnowledgeDetail',
        // ȷ����� vue �ļ�����ʵ���ڵ�
        component: () => import('@/views/KnowledgeDetail.vue'), 
        meta: { title: '֪ʶ����' }
      },
      {
        path: 'guide',
        component: HomeGuide
      },
      {
        path: 'guide/:id',
        component: guideDetail
      },
      {
        path: 'expertGuide',
        component: ExpertGuide
      },
      {
        path: 'expert-dashboard',
        component: ExpertDashboard
      },
      {
        path: 'expertKnowledgeList',
        component: ExpertKnowledgeList
      },
      {
        path: 'allQuestions',
        component: AllQuestions
      },
      {
        path: 'onlineQuestions',
        component: OnlineQuestions
      },
      {
        path: 'myQuestions',
        component: MyQuestions
      },
      {
        path: 'questionDetail/:id',
        component: QuestionDetail
      },
      {
        path: 'askQuestion',
        component: AskQuestion
      },
      {
        path: 'allExpert',
        component: AllExpert
      },
      {
        path: 'question',
        component: question
      },
      {
        path: 'appointment',
        component: appointment
      },
      {
        path: 'shopcart',
        component: ShopCart
      },
      {
        path: 'orderInfo',
        component: OrderInfo
      },
      {
        path: 'order/evaluate',
        component: OrderEvaluate
      },
      {
        path: 'collect',
        component: HomeCollect

      },
      {
        path: 'smartMatch',
        component: SmartMatch
      },
      {
        path: 'smartMatchPage',
        component: SmartMatchPage
      },
      {
        path: 'details',
        component: Details,
      },
      {
        path: 'purchaseDetails',
        component: purchaseDetails,
      },
      {
        path: 'financingDetails',
        component: FinancingDetails,
      },
      {
        path: 'loanProductDetail/:id',
        component: LoanProductDetail,
      },
      {
        path: 'edit-product/:id',
        component: EditProduct,
      },
      {
        path: 'loanApply/overview',
        component: LoanReviewList,
        meta: { roles: ['bank'] }
      },
      {
        path: 'loanApply/:id',
        component: LoanApply,
      },
      {
        path: 'loanApplySuccess',
        component: LoanApplySuccess,
      },
      {
        path: 'chat/:managerId',
        component: Chat,
      },
      {
        path: 'user',
        component: User,
        children: [
          {
            path: '',
            redirect: 'userinfo'
          },
          {
            path: 'userfinance',
            component: UserFinance

          },
          {
            path: 'userinfo',
            component: UserInfo

          },
          {
            path: 'publishedgoods',
            component: PublishedGoods
          },
          {
            path: 'publishedneedsAdmin',
            component: PublishedNeedsAdmin
          },
          {
            path: 'publishedknowledges',
            component: PublishedKnowledges
          },
          {
            path: 'userbuy',
            component: UserBuy
          },
          {
            path: 'usersell',
            component: UserSell
          },
          {
            path: 'expertQuestion',
            component: expertQuestion
          },
          {
            path: 'expertAppoint',
            component: expertAppoint
          },
          {
            path:'expertInfo',
            component: expertInfo
          },
          {
            path: 'myKnowledge',
            component: MyKnowledge
          },
          // {
          //   path: 'publishedgoodsAdmin',
          //   component: PublishedGoodsAdmin
          // },
          {
            path: 'publishedneeds',
            component: PublishedNeeds
          },
        ]
      },
      {
        path:'userGood',
        component:userGood,
        children:[{
          path: 'publishedgoodsAdmin',
          component: PublishedGoodsAdmin
        },{
          path: 'PublishedNeedsAdmin',
          component: PublishedNeedsAdmin
        },]
      },
      {
        path: 'addmessage',
        component: PublishNav,
        children: [
          // {
          //   path: '',
          //   redirect: 'publishgoods'
          // },
          {
            path: 'publishgoods',
            component: PublishGoods
          }, {
            path: 'publishneeds',
            component: PublishNeeds
          },
          {
            path: 'publishknowledges',
            component: PublishKnowledges
          }

        ]
      },{
        path:'aboutUs',
        component:AboutUs
      },{
        path: 'usermanage',
        component: UserManage
      },
    ]
  },
  {
    path: '/payment',
    component: Payment
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/register',
    component: Register
  },
  {
      path: '/message',
      component: Message
    },
    {
        path:'/forget',
        component:forgetBox
      },
]

const router = new VueRouter({
  routes,
  mode: 'hash',
})

router.beforeEach((to, from, next) => {
  const role = store.state.userRole || 'farmer'
  // 统一首页：任何角色都可以访问 /home �? /home/front，不进行角色重定�?
  if (to.path === '/home') {
    return next('/home/front')
  }
  // 对于有角色限制的页面进行判断，不符合角色时统一跳回到首�?
  if (to.meta && to.meta.roles) {
    if (!to.meta.roles.includes(role)) {
      return next('/home/front')
    }
  }
  next()
})

export default router


