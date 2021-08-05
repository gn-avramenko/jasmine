import './index.less'
import 'antd/dist/antd.less'
import "@ant-design/aliyun-theme/index.less"
import React from 'react';
import ReactDOM from 'react-dom';
import { Spin , Layout,Input,Menu,Tabs } from 'antd';
const { Header, Footer, Sider, Content } = Layout;
const { SubMenu } = Menu;
const { TabPane } = Tabs;

let callbackRegistry = new Map()
let callbackIndex = 0

class JasmineReactComponentProxy extends React.Component{

    constructor(props){
       super(props)
       this.state = {
           renderCallback: null,
           version: 0
       }
       this.forceRedraw = this.forceRedraw.bind(this)
    }

    forceRedraw(){
        let newVersion = this.state.version+1
        this.setState({
            version: newVersion
        })
    }

    componentWillUnmount(){
        callbackRegistry.delete(this.props.callbackIndex)
        this.forceRedraw  = null
    }

    render() {
        return callbackRegistry.get(this.props.callbackIndex)()
    }
    
}

 window.ReactFacade ={
     render:ReactDOM.render,
     createElementWrapper: function(elm, props){
       let compRef = React.createRef()
       props.ref = compRef
       return {
          element: React.createElement(elm, props),
          ref: compRef
       }
     },
      createElement: function(elm, props){
        return React.createElement(elm, props)
        },
          createElementWithChildren: function(elm, props, children){
                return React.createElement(elm, props, children)
                },
     createElementWrapperWithChildren: function(elm, props, children){
            let compRef = React.createRef()
            props.ref = compRef
            return {
               element: React.createElement(elm, props,children),
               ref: compRef
            }
     },
     Layout: Layout,
     LayoutHeader:Header,
     LayoutFooter: Footer,
     LayoutSider:Sider,
     LayoutContent:Content,
     Spin:Spin,
     Input:Input,
     Menu:Menu,
     SubMenu:SubMenu,
     MenuItem:Menu.Item,
     Tabs:Tabs,
     TabPane:TabPane,
     createProxy:function(callback){
         let index = callbackIndex++
         callbackRegistry.set(index, callback)
         let compRef = React.createRef()
         let props ={
            ref: compRef,
            callbackIndex: index
         }
         let elm = React.createElement(JasmineReactComponentProxy,props)
         return {element: elm,ref: compRef}
     }
 }

//let compRef = React.createRef()
//let elm = React.createElement(Input,{
//    ref: compRef
//})
//ReactDOM.render(elm, document.getElementById("root"))
//compRef.current.setState({
//    value : "test2"
//})

// class JasmineReactComponentProxy extends React.Component{

//     constructor(props){
//        super(props)
//        this.nestedComponentRef = React.createRef()
//        props.componentSetter(this)
//        props.nestedComponentSetter(this.nestedComponentRef)
//        this.renderCallback = props.renderCallback
//        this.state = {
//            jasmineVersion: 0
//        }
//        this.jasmineRedraw = this.jasmineRedraw.bind(this)
//     }

//     render() {
//         // return this.renderCallback()
//         return React.createElement(Input, {
//             value: "test",
//             ref: this.nestedComponentRef
//         })
//     }

//     jasmineRedraw(){
//         // let currentVersion = this.state.jasmineVersion
//         // this.setState({
//         //     jasmineVersion : currentVersion+1
//         //     }
//         // )
//         this.nestedComponentRef.current.setState({
//             value : "test2"
//         })

//     }

// }



// //ReactDOM.render(
// // <Layout>
// //     <Header>Header</Header>
// //     <Content>Content</Content>
// //     <Footer>Footer</Footer>
// //   </Layout>
// //   ,
// //   document.getElementById('root')
// //   )

// // let elm = React.createElement(Input, {
// //     value: "test"
// // })
// let textValue = "test"
// let component = undefined
// let inputRef = undefined
// let props = {
//     componentSetter: function(value){
//        component = value     
//     },
//     nestedComponentSetter: function(value){
//         inputRef = value     
//      },
//     renderCallback:function(){
//         return React.createElement(Input, {
//                 value: textValue,
//                 ref: inputRef
//             })
//     }
// }
// let elm = React.createElement(JasmineReactComponentProxy, props)
// ReactDOM.render(elm, document.getElementById('root'))
// console.log(elm)
// setTimeout(function(){
//  textValue = "test2"
//  component.jasmineRedraw()
// }, 2000)
// // console.log(inputRef)
// // setTimeout(function(){
// //     inputRef.current.setState({
// //         value: "test2"
// //     })
    
// //    }, 2000)
// //    setTimeout(function(){
// //     console.log(inputRef)   
// //    }, 2000)

