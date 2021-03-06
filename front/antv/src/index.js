import React from 'react';
import ReactDOM from 'react-dom';
import Graphin from '@antv/graphin';

import { ContextMenu } from '@antv/graphin-components';
import './styles.css';
import "@antv/graphin/dist/index.css"; // 引入Graphin CSS
import '@antv/graphin-components/dist/index.css';
import { Row, Col, Layout, AutoComplete, message, Drawer, Typography, Card, Space, Tag, Radio } from 'antd';
import {
  ReadOutlined
} from '@ant-design/icons';

import axios from 'axios';
import { Input } from 'antd';
import {prefix} from './config.js'

const { Header, Content } = Layout;
const { Text } = Typography;
class App extends React.Component {
  constructor() {
    super();
    this.state = {
      included: true,
      visible: false,
      placement: "right",
      nodeTitle: "",
      showNode: {},
      showNodeNote: [],
      showNodeArticle: [],
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

          if (nodes.length === 0) {
            message.info(`oh,你好像没有选中节点...`);
            return
          }
          if (nodes[0].get("model").type == "node") {
            // 获取当前节点的所有关系节点
            axios.post(prefix + "/node/relations", {
              id: nodes[0].get("id")
            }).then((res) => {
              console.log(nodes[0].get("model"))
              // 根据关系节点获取关系节点上关联的文章
              this.setState({
                showNode: {
                  data: res.data,
                  node: nodes[0].get("model"),
                  type: "node"
                }
              })
            });

            // 获取当前节点的文章
            axios.post(prefix + "/article/node/articles", {
              id: nodes[0].get("id"),
              type: 1
            }).then((res) => {
              // 根据关系节点获取关系节点上关联的文章
              this.setState({
                showNodeArticle: res.data.articles
              })
            });

            // 获取当前节点的注解
            axios.post(prefix + "/node/note/relation", {
              id: nodes[0].get("id"),
              type: 1
            }).then((res) => {
              // 根据关系节点获取关系节点上关联的文章
              this.setState({
                showNodeNote: res.data
              })
            });

          }


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
    this.onIncludedChange = this.onIncludedChange.bind(this);
    this.getNodeList(0, true);
  }


  render() {
    return (

      <Layout>
        <Header>Header</Header>
        <Content>
          <Row justify="space-around">
            <Col span={8}>

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
          <Row justify="space-around">
            <Col span={8}>
              <Radio.Group onChange={this.onIncludedChange} value={this.state.included}>
                <Radio value={true}>包含</Radio>
                <Radio value={false}>关联</Radio>
              </Radio.Group>
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
        {this.state.showNode.node && (
          <Drawer
            width={720}
            title={this.state.showNode.node.label}
            placement={this.state.placement}
            closable={false}
            onClose={this.onClose}
            visible={this.state.visible}
          >
            <Space direction="vertical" className="drawer-space">

              {
                this.state.showNodeNote.length > 0 && <Text>本节点注解:</Text>
              }
              {
                this.state.showNodeNote && this.state.showNodeNote.map((item, index) => {
                  return <Text key={index.toString()}>{item.content} </Text>
                })
              }

              {
                this.state.showNodeArticle.length > 0 && <Text>本节点文章:</Text>
              }
              
              {
                this.state.showNodeArticle && this.state.showNodeArticle.map((article, index) => {
                  return <Text key={index.toString()}><a target="_blank" href={article.url}>{article.title}</a> </Text>
                })
              }

              {

                this.state.showNode.data.map((item, index) => {

                  return <Space direction="vertical" key={index.toString()} className="space">
                    <Text> 关系名称：{item.relationRemark ? item.relation + '(' + item.relationRemark + ')' : item.relation}</Text>
                    <Text >相关知识点:</Text >
                    <div>
                      {
                        item.list.map((node, i) => {
                          return <Tag key={i.toString()}>{node.nodeName}</Tag  >
                        })
                      }
                    </div>
                    {
                      item.notes.length > 0 && <Text >相关注解</Text>
                    }
                    {item.notes.map((note, i) => {
                      return <Text key={i.toString()}>{note.content}</Text>
                    })}
                    {
                      item.articles.length > 0 && <Text >相关文章</Text>
                    }
                    {item.articles.map((article, i) => {
                      return <Text key={i.toString()}><a target="_blank" href={article.url}>{article.title}</a></Text>
                    })}
                  </Space>
                })

              }
            </Space>
          </Drawer>
        )
        }
      </Layout>

    );

  }

  onIncludedChange(e) {
    this.setState({
      included: e.target.value,
    })
  }

  onClose() {
    this.setState({
      visible: false,
    });
  };

  onSelect(value, option) {
    this.getNodeList(option.id, this.state.included)
  }

  handleSearch(value) {
    axios.post(prefix + "/node/list", {
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
    axios.post(prefix + "/front", {
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
    axios.post(prefix + "/front", {
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