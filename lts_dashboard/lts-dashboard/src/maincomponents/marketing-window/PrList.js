/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import {withStyles} from 'material-ui/styles';
import Paper from 'material-ui/Paper';
import Divider from 'material-ui/Divider';
import SvgIcon from 'material-ui/SvgIcon';
import {
    DataTypeProvider,
    FilteringState,
    IntegratedFiltering,
    IntegratedSorting,
    SortingState
} from "@devexpress/dx-react-grid";
import {Grid, TableFilterRow, TableHeaderRow} from '@devexpress/dx-react-grid-material-ui'
import MilestoneCheckButton from "./milestones/MilestoneButton.js"
import {
    TableColumnResizing,
    VirtualTable
} from "@devexpress/dx-react-grid-material-ui/dist/dx-react-grid-material-ui.cjs";
import Chip from "material-ui/es/Chip/Chip";

const styles = theme => ({
    root: {
        width: '100%',
    },

    paper: theme.mixins.gutters({
        paddingTop: 16,
        paddingBottom: 16,
        marginTop: theme.spacing.unit * 3,
        width: `97%`,
        marginRight: `5%`,
        height: 700
    }),
    appBar: {
        paddingBottom: 20
    }
});

// indicators
const ValidIcon = (props) => (
    <SvgIcon {...props}>
        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
    </SvgIcon>
);


const InvalidIcon = (props) => (
    <SvgIcon {...props}>
        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
    </SvgIcon>
);

// formatters
const PrLinkFormatter = ({value}) =>
    <a href="#" onClick={() => window.open(value["html_url"], '_blank')}>{value["title"]}</a>;

PrLinkFormatter.propTypes = {
    value: PropTypes.object.isRequired,
};

const PrLinkTypeProvider = props => (
    <DataTypeProvider
        formatterComponent={PrLinkFormatter}
        {...props}
    />
);

const LabelFormatter = ({value}) =>
    <div>
        {
            value.map(function (label) {
                return(<Chip label={label}/>);
            })
        }
    </div>;

LabelFormatter.propTypes = {
    value: PropTypes.array.isRequired
};

const LabelTypeProvider = props => (
    <DataTypeProvider
        formatterComponent={LabelFormatter}
        {...props}
    />
);

const ValidFormatter = ({value})=>
{
  if(value){
      return (
          <ValidIcon color="primary"/>
      )
  }  else {
      return (
          <InvalidIcon color="error"/>
      )
  }
};

ValidFormatter.prototype = {
    value : PropTypes.bool.isRequired
};

const ValidTypeProvider = props => (
    <DataTypeProvider
        formatterComponent={ValidFormatter}
        {...props}
    />
);

// filters
const toLowerCase = value => String(value).toLowerCase();
const milestonePredicate = (value, filter) => toLowerCase(value["mObject"]["title"]).startsWith(toLowerCase(filter.value));
const prPredicate = (value, filter) => toLowerCase(value["title"]).startsWith(toLowerCase(filter.value));


// sorting function
const compareText = (a, b) => {
    a = a["title"];
    b = b["title"];

    if (toLowerCase(a) > toLowerCase(b)) {
        return 1;
    }
    else if (toLowerCase(a) < toLowerCase(b)) {
        return -1
    }
    return 0;
};

function getColumnWidths() {
    let screenWidth = window.innerWidth;
    let divPaperSize = Math.round(screenWidth / 100 * 92);
    let col1Size = Math.round(divPaperSize / 100 * 40);
    let col2Size = Math.round(divPaperSize / 100 * 20);
    let col3Size = Math.round(divPaperSize / 100 * 20);
    let col4Size = Math.round(divPaperSize / 100 * 10);
    let col5Size = Math.round(divPaperSize / 100 * 10);
    return [col1Size, col2Size, col3Size, col4Size, col5Size]
}

class PrList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            prList: [],
            displayPrList: [],
            integratedFilteringColumnExtensions: [
                {columnName: 'title', predicate: prPredicate},

            ],
            integratedSortingColumnExtensions: [
                {columnName: 'title', compare: compareText},
            ],
            issueTitleCol: ["title"],
            labelColumn : ["labels"],
            validColumn: ["valid"]
        };

        this.modalOpen = this.modalOpen.bind(this);
    }


    componentWillUpdate(nextProps, nextState) {
        if (nextProps.prList !== this.state.prList) {
            this.setState({
                prList: nextProps.prList,
                displayPrList: this.processPrList(nextProps.prList),
            })
        }
    }


    modalOpen(data) {
        this.props.modalLauch(data);
    };

    processPrList(prList) {
        console.log(prList);
        let displayArray = [];
        prList.forEach(function (element) {
                let prTitle = {"html_url": element["url"], "title": element["title"]};
                let user = element["user"];
                let valid = element["validMarketing"];
                let branchData = element["repoName"]+" : "+element["branch"];
                let labels = element["labels"];

                let prData = {
                    title: prTitle,
                    user: user,
                    valid: valid,
                    branchData: branchData,
                    labels : labels
                };
                displayArray.push(prData);
            }
        );

        return displayArray;

    }


    render() {
        const {classes} = this.props;
        let columnSizes = getColumnWidths();
        const {integratedSortingColumnExtensions, integratedFilteringColumnExtensions} = this.state;

        return (
            <Paper className={classes.paper} elevation={4}>
                <Divider light/>
                <div className={classes.root}>
                    <Grid
                        rows={this.state.displayPrList}

                        columns={[
                            {name: 'title', title: 'Feature'},
                            {name: 'user', title: 'User'},
                            {name: 'labels', title: 'Labels'},
                            {name: 'branchData', title: 'Branch'},
                            {name: 'valid', title: 'Marketing Message Validity'}
                        ]}>

                        <FilteringState defaultFilters={[]}/>
                        <IntegratedFiltering columnExtensions={integratedFilteringColumnExtensions}/>

                        <SortingState
                            defaultSorting={[{columnName: 'milestone', direction: 'desc'}]}
                        />
                        <IntegratedSorting columnExtensions={integratedSortingColumnExtensions}/>

                        <VirtualTable height={700}/>
                        {/*<TableColumnResizing defaultColumnWidths={[*/}
                            {/*{columnName: 'title', width: columnSizes[0]},*/}
                            {/*{columnName: 'user', width: columnSizes[1]},*/}
                            {/*{columnName: 'labels', width: columnSizes[2]},*/}
                            {/*{columnName: 'valid', width: columnSizes[5]},*/}
                            {/*{columnName: 'branchData', width: columnSizes[4]},*/}

                        {/*]}/>*/}

                        <PrLinkTypeProvider
                            for={this.state.issueTitleCol}
                        />

                        <LabelTypeProvider
                            for={this.state.labelColumn}
                        />

                        <ValidTypeProvider
                            for={this.state.validColumn}
                        />
                        <TableHeaderRow showSortingControls/>
                        <TableFilterRow/>

                    </Grid>
                </div>
            </Paper>

        );
    }
}

PrList.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(PrList);