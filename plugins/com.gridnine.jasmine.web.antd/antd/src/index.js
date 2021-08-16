import 'antd/dist/antd.compact.less'
//import "@ant-design/aliyun-theme/index.less"
import React from 'react';
import ReactDOM from 'react-dom';
import { Spin , Layout,Input,Menu,Tabs, Dropdown,Button,Table,Select,Tooltip,DatePicker,InputNumber,notification,Modal,Switch,Collapse,Tree} from 'antd';
import debounce from "lodash/debounce";
import {LinkOutlined,EyeInvisibleOutlined, EyeTwoTone, CloseOutlined,PlusOutlined,UpOutlined,DownOutlined,MinusOutlined} from '@ant-design/icons';
import moment from 'moment';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
const { Header, Footer, Sider, Content } = Layout;
const { SubMenu } = Menu;
const { TabPane } = Tabs;
const { Search } = Input;
const { Option } = Select;
const {Panel} = Collapse;
const { TreeNode } = Tree;


let callbackRegistry = new Map()
let callbackIndex = 0

class JasmineReactComponentProxy extends React.Component{

    constructor(props){
       super(props)
       this.state = {
           version: 0
       }
       this.forceRedraw = this.forceRedraw.bind(this)
       this.getCallbacks = this.getCallbacks.bind(this)
    }

    forceRedraw(){
        let newVersion = this.state.version+1
        this.setState({
            version: newVersion
        })
    }

    getCallbacks(){
      if(this.props.parentIndex != null){
         return callbackRegistry.get(this.props.parentIndex).get(this.props.callbackIndex) 
      }
      return callbackRegistry.get(this.props.callbackIndex)
    }

    componentDidUpdate(prevProps) {
      let callbacks = this.getCallbacks()
      if(callbacks.componentDidUpdate){
          callbacks.componentDidUpdate(prevProps)
      }
    }

    componentDidMount(){
        let callbacks = this.getCallbacks()
        if(callbacks.componentDidMount){
            callbacks.componentDidMount(this)
        }
    }

    componentWillUnmount(){
        let callbacks = this.getCallbacks()
        if(callbacks.componentWillUnmount){
            callbacks.componentWillUnmount(this)
        }
        if(this.props.parentIndex != null){
          return
        }
        callbackRegistry.delete(this.props.callbackIndex)
        this.forceRedraw  = null
    }

    render() {
      return this.getCallbacks().renderCallback(this.props.parentIndex, this.props.callbackIndex)
    }
}

function DebounceSelect({ fetchOptions, debounceTimeout = 800, ...props }) {
  const [fetching, setFetching] = React.useState(false);
  const [initialized, setInitialized] = React.useState(false);
  const [options, setOptions] = React.useState([]);
  const fetchRef = React.useRef(0);
  const debounceFetcher = React.useMemo(() => {
    const loadOptions = (value) => {
      fetchRef.current += 1;
      const fetchId = fetchRef.current;
      setOptions([]);
      setFetching(true);
      fetchOptions(value, (newOptions) => {
        if (fetchId !== fetchRef.current) {
          // for fetch callback order
          return;
        }

        setOptions(newOptions);
        setFetching(false);
        setInitialized(true);
      });
    };

    return debounce(loadOptions, debounceTimeout);
  }, [fetchOptions, debounceTimeout]);
  const handleOnDropdownVisibleChange = (open) => {
    if (!open) {
      setInitialized(false);
      return;
    }
    if (fetching || initialized) {
      return;
    }
    fetchRef.current += 1;
    const fetchId = fetchRef.current;
    setOptions([]);
    setFetching(true);
    fetchOptions("", (newOptions) => {
      if (fetchId !== fetchRef.current) {
        // for fetch callback order
        return;
      }

      setOptions(newOptions);
      setFetching(false);
      setInitialized(true);
    });
  };
  return (
    <Select
      labelInValue
      onSearch={debounceFetcher}
      onDropdownVisibleChange={handleOnDropdownVisibleChange}
      notFoundContent={fetching ? <Spin size="small" /> : null}
      {...props}
      options={options}
    />
  );
} 

 window.ReactFacade ={
     incrementAndGetCallbackIndex: function(){
       callbackIndex++;
       return callbackIndex
     },
     getCallbacks: function(parentIndex, childIndex){
      if(parentIndex != null){
        return callbackRegistry.get(parentIndex).get(childIndex) 
     }
     return callbackRegistry.get(childIndex)
    },
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
     callbackRegistry:callbackRegistry,
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
     Dropdown:Dropdown,
     Button:Button,
     Fragment:React.Fragment,
     Search:Search,
     Table:Table,
     DebounceSelect:DebounceSelect,
     Select:Select,
     SelectOption:Option,
     Tooltip:Tooltip,
     IconLinkOutlined:LinkOutlined,
     DatePicker:DatePicker,
     IconEyeInvisibleOutlined: EyeInvisibleOutlined, 
     IconEyeTwoTone:EyeTwoTone,
     PasswordBox:Input.Password,
     InputNumber:InputNumber,
     notification:notification,
     Modal:Modal,
     Switch:Switch,
     Collapse:Collapse,
     Panel:Panel,
     ReactQuill:ReactQuill,
     Tree:Tree,
     TreeNode: TreeNode,
     IconCloseOutlined:CloseOutlined,
     IconPlusOutlined:PlusOutlined,
     IconUpOutlined:UpOutlined,
     IconDownOutlined:DownOutlined,
     IconMinusOutlined:MinusOutlined,
     createProxyAdvanced:function(parentIndex, renderCallback, otherCallbacks){
         let allCallbacks = otherCallbacks || {}
         allCallbacks.renderCallback = renderCallback;
         let map = null;
         if(parentIndex != null){
           map = callbackRegistry.get(parentIndex);
           if(map == null){
              map =  new Map();
              callbackRegistry.set(parentIndex, map);
           }
         } else {
           map = callbackRegistry;
         }
         callbackIndex++;
         let index = callbackIndex
         map.set(index, allCallbacks)
         let compRef = React.createRef()
         let props ={
            ref: compRef,
            callbackIndex: index,
            parentIndex: parentIndex,
         }
         let elm = React.createElement(JasmineReactComponentProxy,props)
         return {element: elm,ref: compRef}
     },
     createProxy:function(parentIndex, renderCallback){
        return this.createProxyAdvanced(parentIndex, renderCallback, null)        
    },
    dateToMoment:function(date){
        return date? moment(date): null
    }, 
    momentToDate:function(moment){
        if(!moment){
            return null
        }
        return new Date(moment.year(), moment.month(), moment.date())
    },
    momentToDateTime:function(moment){
        if(!moment){
            return null
        }
        return new Date(moment.year(), moment.month(), moment.date(), moment.hour(), moment.minute(), moment.second())
    }
 }

