import React from 'react';
import ReactDOM from 'react-dom';
import Graphin from '@antv/graphin';

import { ContextMenu } from '@antv/graphin-components';
import './styles.css';
import "@antv/graphin/dist/index.css"; // 引入Graphin CSS
import { Row, Col, Layout, AutoComplete, message } from 'antd';
import axios from 'axios';
import { Input } from 'antd';
const { Header, Content } = Layout;
class App extends React.Component {
  constructor() {
    super();
    this.state = {
      data: {
        nodes: [],
        nodesMap: {},
        edgesMap: {}
      },
      options: [],
      menus: [{
        key: "discovery",
        title: "扩展关系",
        visible: true,
        iconType: "select",
        onClick: (e) => {

          const nodes = e.graph.findAllByState('node', 'selected');
          if (nodes.length === 0) {
            message.info(`oh,你好像没有选中节点...`);
          } else {
            const nodeId = nodes[0].get('id');
            this.addNodeList(nodeId, false);
          }
        }
      }]
    }
    this.onSelect = this.onSelect.bind(this);
    this.handleSearch = this.handleSearch.bind(this);
    this.getNodeList = this.getNodeList.bind(this);
    this.addNodeList = this.addNodeList.bind(this);
  }


  render() {
    return (

      <Layout>
        <Header>Header</Header>
        <Content>
          <Row justify="space-around">
            <Col span={6}>
              <AutoComplete
                dropdownMatchSelectWidth={252}
                style={{ width: 300 }}
                options={this.state.options}
                onSelect={this.onSelect}
              >
                <Input.Search size="large" placeholder="input here" enterButton onSearch={this.handleSearch} />
              </AutoComplete>
            </Col>
          </Row>
          <Graphin data={this.state.data}
            layout={{
              name: 'force',
              options: {
                defSpringLen: (_edge, source, target) => {
                  /** 默认返回的是 200 的弹簧长度 */
    
                  /** 如果你要想要产生聚类的效果，可以考虑 根据边两边节点的度数来动态设置边的初始化长度：度数越小，则边越短 */
                  const nodeSize = 30;
                  const Sdegree = source.data.layout?.degree;
                  const Tdegree = target.data.layout?.degree;
                  const minDegree = Math.min(Sdegree, Tdegree);
                  return minDegree < 3 ? nodeSize * 5 : minDegree * nodeSize * 2;
                },
              },
            }} >

            <ContextMenu options={this.state.menus} />
          </Graphin>
        </Content>
      </Layout>

    );

  }

  onSelect(value, option) {
    this.getNodeList(option.id, true)
  }

  handleSearch(value) {
    axios.post("http://localhost:8089/node/list", {
      content: value
    }).then((res) => {
      this.setState({
        options: res.data.filter(i => i.type === 'node').map(i => {
          return {
            id: i.id,
            value: i.name
          }
        })
      })
    })
  }
  addNodeList(id, isInclude) {
    axios.post("http://localhost:8089/front", {
      id: id,
      isInclude: isInclude
    }).then((res) => {
      const data = res.data
      let nodes = Object.assign([], this.state.data.nodes);
      let edges = Object.assign([], this.state.data.edges);
      let nodesMap = Object.assign([], this.state.data.nodesMap);
      let edgesMap = Object.assign([], this.state.data.edgesMap);
      data.nodes.forEach(node => {
        if (nodesMap[node.id]) {
          return;
        }
        let nodeObj = {
          id: node.id,
          label: node.name,
          type: node.type,
          data: {
            id: node.id,
            label: node.name,
            type: node.type
          },
          shape: "cricle",
          style: {
            fill: "white"
          }
        }
        nodesMap[node.id] = nodeObj
        nodes.push(nodeObj);
      });
      data.edges.forEach(edge => {
        if (edgesMap[edge.id]) {
          return;
        }
        let edgeObj = {
          eid: edge.id,
          source: edge.source,
          target: edge.target,
          label: edge.type,
          data: {
            source: edge.source,
            target: edge.target,
            label: edge.type,
          },
          shape: "line",
          style: {
            stroke: "blue"
          }
        }
        edgesMap[edge.id] = edgeObj
        edges.push(edgeObj);
      });
      this.setState({
        data: {
          edges: edges,
          nodes: nodes,
          nodesMap: nodesMap,
          edgesMap: edgesMap
        }
      });
    })
  }
  getNodeList(id, isInclude) {
    axios.post("http://localhost:8089/front", {
      id: id,
      isInclude: isInclude
    }).then((res) => {
      const data = res.data
      let nodes = Object.assign([], this.state.data.nodes);
      let edges = Object.assign([], this.state.data.edges);
      let nodesMap = Object.assign([], this.state.data.nodesMap);
      let edgesMap = Object.assign([], this.state.data.edgesMap);
      data.nodes.forEach(node => {
        if (nodesMap[node.id]) {
          return;
        }
        let nodeObj = {
          id: node.id,
          label: node.name,
          type: node.type,
          data: {
            id: node.id,
            label: node.name,
            type: node.type
          },
          shape: "cricle",
          style: {
            fill: "white"
          }
        }
        nodesMap[node.id] = nodeObj
        nodes.push(nodeObj);
      });
      data.edges.forEach(edge => {
        if (edgesMap[edge.id]) {
          return;
        }
        let edgeObj = {
          eid: edge.id,
          source: edge.source,
          target: edge.target,
          label: edge.type,
          data: {
            source: edge.source,
            target: edge.target,
            label: edge.type,
          },
          shape: "line",
          style: {
            stroke: "blue"
          }
        }
        edgesMap[edge.id] = edgeObj
        edges.push(edgeObj);
      });
      console.log(nodes)
      console.log(edges)
      this.setState({
        data: {
          edges: edges,
          nodes: nodes,
          nodesMap: nodesMap,
          edgesMap: edgesMap
        }
      });
    })
  }
};

const rootElement = document.getElementById('root');
ReactDOM.render(<App />, rootElement);