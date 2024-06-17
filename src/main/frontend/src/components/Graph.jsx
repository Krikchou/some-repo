import React, { useEffect, useState } from "react";
import functionPlot from "function-plot";
import { Button } from "react-bootstrap";


const Graph = ({data, id}) => {

    const [dots, setDots] = useState(false);

    const renderFunction = () => {
        let functions = [];
        data.distributionFunctions.forEach(e => {
            functions.push({
                fn: e.first,
                range: [e.second.first, e.second.second]
            })
        })


        functionPlot({
            target: `#something-${id}`,
            data: functions
        }).draw()
    }

    
    const renderDots = () => {
        let points = [];
        data.dots.forEach(e => {
            points.push(
                [e.first , e.second]
            )
        })


        functionPlot({
            target: `#something-${id}`,
            data: [{
                points: points,
                fnType: 'points',
                graphType: 'scatter'
        },
        {
            points: [[data.centoroid.first, data.centoroid.second]],
            fnType: 'points',
            graphType: 'scatter'
    }
    ]
        }).draw()
    }

    useEffect(()=>{
        if(dots) {
            renderDots();
        } else {
            renderFunction();
        }
    })

    return <div>
         <div id={`something-${id}`}/>
        <div>{data.forProperty}</div>
        <Button onClick={() => setDots(!dots)}>
            Change View
        </Button>
    </div>
}

export default Graph;