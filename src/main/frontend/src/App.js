import './App.css';
import Graph from './components/Graph';

function App() {
  const DUMMY = {
    forProperty: "record2",
    centoroid: {
      first: 42.888888888888886,
      second: 1
    },
    expectedDensity: 3398.8422291402508,
    dots: [
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 36,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 40,
        second: 1
      },
      {
        first: 44,
        second: 1
      },
      {
        first: 46,
        second: 1
      },
      {
        first: 45,
        second: 1
      },
      {
        first: 42,
        second: 1
      },
      {
        first: 47,
        second: 1
      }
    ],
    distributionFunctions: [
      {
        first: "-0.0*x+0.0",
        second: {
          first: 0,
          second: 0.6888888888888887
        }
      },
      {
        first: "-0.0*x+25.0",
        second: {
          first: 0.6888888888888887,
          second: 1.3777777777777773
        }
      },
      {
        first: "-0.0*x+0.0",
        second: {
          first: 1.3777777777777773,
          second: 2.066666666666666
        }
      },
      {
        first: "-36.290322580645174*x+100.0",
        second: {
          first: 2.066666666666666,
          second: 2.7555555555555546
        }
      },
      {
        first: "-0.0*x+50.0",
        second: {
          first: 2.7555555555555546,
          second: 3.444444444444443
        }
      },
      {
        first: "36.29032258064515*x+-124.99999999999991",
        second: {
          first: 3.444444444444443,
          second: 4.133333333333332
        }
      },
      {
        first: "-0.0*x+0.0",
        second: {
          first: 4.133333333333332,
          second: 4.82222222222222
        }
      },
      {
        first: "-0.0*x+0.0",
        second: {
          first: 4.82222222222222,
          second: 5.511111111111109
        }
      },
      {
        first: "-0.0*x+0.0",
        second: {
          first: 5.511111111111109,
          second: 6.1999999999999975
        }
      }
    ]
  }


  return (
    <div className="App">
      <Graph data={DUMMY} id={2}/>
    </div>
  );
}

export default App;
