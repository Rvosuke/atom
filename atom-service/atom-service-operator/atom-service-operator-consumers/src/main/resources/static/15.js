(window.webpackJsonp=window.webpackJsonp||[]).push([[15],{404:function(t,e,n){"use strict";n.r(e);var i=n(506),a=n(443);for(var o in a)["default"].indexOf(o)<0&&function(t){n.d(e,t,(function(){return a[t]}))}(o);var r=n(0),u=Object(r.a)(a.default,i.a,i.b,!1,null,null,null);e.default=u.exports},415:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=o(n(6)),a=o(n(20));function o(t){return t&&t.__esModule?t:{default:t}}e.default={data:function(){var t=this;return{searchFilter:{},tableInfo:{data:[],rowHandle:{fixed:"right",width:"90px",custom:[{text:"删除",type:"text",size:"mini",emit:"cutOff"}]},columns:[],pagination:{currentPage:1,pageSize:10,total:0,background:!0,layout:"total,-> , prev, pager, next"},events:{modify:function(e){t.$refs.editSimplePopup.handleCreate(JSON.parse((0,a.default)(e.row)))},otterEmit:function(e){t.otterEmitFn(e.row)},cutOff:function(e){t.$confirm("此操作将删除该信息, 是否继续?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then((function(){t.HTTPCommon.updateFn({id:e.row.id,deleteFlag:1}).then((function(e){t.$message.success("操作成功"),t.getList()})).catch((function(t){}))})).catch((function(){}))},view:function(e){t.lookView(e)}}},editPopupInfo:{drawer:{attrs:{title:"新增",direction:"rtl"},events:{beforeClose:function(t){console.log("关闭弹框")}}},form:{width:"120px",inline:!0,items:[]},events:{confirm:function(e){console.log("修改点击确定按钮==>",e),t.modifyOrganization(e)},cancel:function(){t.$refs.editSimplePopup.handleClose()}}}}},created:function(){this.initParams()},mounted:function(){this.getList()},methods:{initParams:function(){var t=localStorage.getItem("organization");if(this.searchFilter={parentId:1},t){var e=JSON.parse(t);this.searchFilter.spaceId=e.id}},lookView:function(t){console.log(t)},paginationCurrentChange:function(t){this.tableInfo.pagination.currentPage=t,this.getList()},getList:function(){var t=this,e=(0,i.default)({},this.tableInfo.pagination,this.searchFilter);this.HTTPCommon.getList(e).then((function(e){t.tableInfo.pagination.total=e.total,t.tableInfo.pagination.currentPage=e.currentPage,t.tableInfo.pagination.pageSize=e.pageSize,t.tableInfo.data=e.data||[]}))},modifyOrganization:function(t){var e=this;this.HTTPCommon.insertFn(t).then((function(t){e.getList()})).catch((function(t){}))},addPm:function(){this.$refs.editSimplePopup.handleCreate()},tableKeyToHump:function(){var t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:[];return t.map((function(t){return t.key=t.key.replace(/\_(\w)/g,(function(t,e){return e.toUpperCase()})),t.minWidth=t.minWidth||"100",t.align=t.align||"center",t.isNoEdit||(t.label=t.label||t.title,t.props=t.props||t.key,t.type=t.type||"input"),(0,i.default)({},t)}))}}}},443:function(t,e,n){"use strict";n.r(e);var i=n(444),a=n.n(i);for(var o in i)["default"].indexOf(o)<0&&function(t){n.d(e,t,(function(){return i[t]}))}(o);e.default=a.a},444:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=l(n(6)),a=n(152),o=l(n(415)),r=l(n(193)),u=l(n(195));function l(t){return t&&t.__esModule?t:{default:t}}e.default={name:"operatorIndex",components:{SimplePopup:r.default},mixins:[o.default],data:function(){return{HTTPCommon:u.default,searchFilter:{organizationType:"EXPERIMENT"}}},created:function(){localStorage.getItem("organization");this.searchFilter={organizationType:"EXPERIMENT",parentId:this.$route.query.pid};var t=[{title:"创建人",key:"createName"},{title:"创建人ID",key:"createId"},{title:"解释",key:"explaination"},{title:"别名",key:"organizationAlias"},{title:"名称",key:"organizationName"},{title:"状态",key:"organizationStatus",type:"select",attrs:{placeholder:"请选择状态"},options:[{label:"CREATING",value:"CREATING"},{label:"CREATE_FINISH",value:"CREATE_FINISH"},{label:"CREATE_CANCEL",value:"CREATE_CANCEL"}]},{title:"拥有人ID",key:"ownerId"},{title:"拥有人名",key:"ownerName"},{title:"创建时间",key:"createTime",width:160,isNoEdit:!0,formatter:function(t){return(0,a.parseTime)(t.createTime)||"-"}},{title:"更新时间",key:"updateTime",width:160,isNoEdit:!0,formatter:function(t){return(0,a.parseTime)(t.createTime)||"-"}}];this.tableInfo.columns=this.tableKeyToHump(t);var e=t.filter((function(t){return!t.isNoEdit}));this.editPopupInfo.form.items=e},methods:{modifyOrganization:function(t){var e=this;this.HTTPCommon.insertFn((0,i.default)({},t,{parentId:this.searchFilter.parentId,organizationType:"EXPERIMENT"})).then((function(t){e.$refs.editSimplePopup.handleClose(),e.$message.success("新增成功"),e.getList()})).catch((function(t){}))}}}},506:function(t,e,n){"use strict";n.d(e,"a",(function(){return i})),n.d(e,"b",(function(){return a}));var i=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"m-20 p-12 bg-white"},[n("el-button",{attrs:{type:"primary",size:"small"},on:{click:t.addPm}},[t._v("新增")]),t._v(" "),n("d2-crud",t._g(t._b({on:{"pagination-current-change":t.paginationCurrentChange}},"d2-crud",t.tableInfo,!1),t.tableInfo.events)),t._v(" "),t.editPopupInfo?n("simple-popup",t._g({ref:"editSimplePopup",attrs:{settings:t.editPopupInfo}},t.editPopupInfo.events)):t._e()],1)},a=[]}}]);