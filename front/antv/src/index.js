import React from 'react';
import ReactDOM from 'react-dom';
import Graphin from '@antv/graphin';

import { ContextMenu } from '@antv/graphin-components';
import './styles.css';
import "@antv/graphin/dist/index.css"; // 引入Graphin CSS
import '@antv/graphin-components/dist/index.css';
import { Row, Col, Layout, AutoComplete, message, Drawer, Button, Radio, Space } from 'antd';

import axios from 'axios';
import { Input } from 'antd';
const { Header, Content } = Layout;
class App extends React.Component {
  constructor() {
    super();
    this.state = {
      visible: false,
      placement: "right",
      nodeTitle: "",
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
        onClick: (e) => {

          const nodes = e.graph.findAllByState('node', 'selected');

          if (nodes.length === 0) {
            message.info(`oh,你好像没有选中节点...`);
          } else {
            if (nodes[0].get('model').type != 'node') {
              message.info(`关系节点无法扩展，请选择一般节点`);
            } else {
              const nodeId = nodes[0].get('id');
              this.addNodeList(nodeId, false);
            }
          }
        }
      },
      {
        key: "detail",
        title: "节点明细",
        visible: true,
        onClick: (e) => {
          const nodes = e.graph.findAllByState('node', 'selected');
          console.log(nodes)
          this.setState({
            visible: true,
          });
        }
      }]
    }
    this.onSelect = this.onSelect.bind(this);
    this.handleSearch = this.handleSearch.bind(this);
    this.getNodeList = this.getNodeList.bind(this);
    this.addNodeList = this.addNodeList.bind(this);
    this.onClose = this.onClose.bind(this);
    this.getNodeList(0, true);
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
          <Graphin className="graphin" data={this.state.data}
            layout={{
              name: 'force',
              options: {
                defSpringLen: (_edge, source, target) => {

                  if (target.data.type == "relation" && target.data.layout.degree == 2) {
                    return 50;
                  }

                  if (source.data.type == "relation" && source.data.layout.degree == 2) {
                    return 50;
                  }

                  if (source.data.type == "relation") {
                    return 20
                  }

                  return 200
                },
              },
            }} >

            <ContextMenu options={this.state.menus} />
          </Graphin>
        </Content>
        <Drawer
          title={this.state.nodeTitle}
          placement={this.state.placement}
          closable={false}
          onClose={this.onClose}
          visible={this.state.visible}
        >
          <p>Some contents...</p>
          <p>Some contents...</p>
          <p>Some contents...</p>
        </Drawer>
      </Layout>

    );

  }

  onClose() {
    this.setState({
      visible: false,
    });
  };

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
        let nodeSize, primaryColor;
        if (node.type == 'node') {
          nodeSize = 40
          primaryColor = "#91D5FF"
        } else {
          nodeSize = 5
          primaryColor = "#535353"
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
          shape: "CircleNode",
          style: {
            nodeSize: nodeSize,
            primaryColor: primaryColor,
            fontSize: 12
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
          label: "",
          data: {
            source: edge.source,
            target: edge.target,
            label: edge.type,
          },
          shape: "line",
          style: {
            stroke: "#535353"
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
  computeDegree(edges) {
    var degree = {}
    for (var i = 0; i < edges.length; i++) {
      let edge = edges[i]
      degree[edge.source] = degree[edge.source] ? degree[edge.source] + 1 : 1
      degree[edge.target] = degree[edge.target] ? degree[edge.target] + 1 : 1
    }
    return degree
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
        let nodeSize, primaryColor;
        if (node.type == 'node') {
          nodeSize = 40
          primaryColor = "#91D5FF"
        } else {
          nodeSize = 5
          primaryColor = "#535353"
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
          shape: "CircleNode",
          style: {
            nodeSize: nodeSize,
            primaryColor: primaryColor,
            fontSize: 12
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
          label: "",
          data: {
            source: edge.source,
            target: edge.target,
            label: edge.type,
          },
          shape: "line",
          style: {
            stroke: "#535353"
          }
        }
        edgesMap[edge.id] = edgeObj
        edges.push(edgeObj);
      });
      var degreeMap = this.computeDegree(edges)
      edges.forEach(edge => {
        let target = nodesMap[edge.target]
        if (target.type == "relation" && degreeMap[target.id] > 2) {
          edge.style.stroke = "red"
        }
      })
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