import React from 'react';
import ReactDOM from 'react-dom';
import Graphin, {Utils} from '@antv/graphin';

import "@antv/graphin/dist/index.css"; // 引入Graphin CSS
import './styles.css';

import axios from 'axios';

class App extends React.Component {
  constructor() {
    super();
    this.state = {
      data: {
        nodes: []
      }
    }
    axios.post("http://localhost:8089/front", {
      id: 11
    }).then((res) => {
      const data = res.data
      data.nodes = data.nodes.map(node => {
        return {
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
        };
      });
      data.edges = data.edges.map(edge => {
        return {
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
        };
      });

      this.setState({ data: data });

    })
  }

  showData() {

  }

  render() {
    return (
      <div className="App">
        <Graphin data={this.state.data}
          layout={{
            name: 'force',
            options: {
              defSpringLen: (_edge, source, target) => {
                const nodeSize = 30;
                const Sdegree = source.data.layout?.degree;
                const Tdegree = target.data.layout?.degree;
                const minDegree = Math.min(Sdegree, Tdegree);
                /** 根据边两边节点的度数来动态设置边的初始化长度：度数越小，则边越短，能够产生聚类效果 */
                return minDegree < 2 ? nodeSize : minDegree * nodeSize * 2;
              },
            },
          }} />
      </div>
    );
  }
};

const rootElement = document.getElementById('root');
ReactDOM.render(<App />, rootElement);