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
import ExpansionPanel, {ExpansionPanelDetails, ExpansionPanelSummary,} from 'material-ui/ExpansionPanel';
import Typography from 'material-ui/Typography';
import ExpandMoreIcon from 'material-ui-icons/ExpandMore';
import Divider from "material-ui/es/Divider/Divider";
import List from "material-ui/es/List/List";
import ListItem from "material-ui/es/List/ListItem";
import ListItemIcon from "material-ui/es/List/ListItemIcon";
import ListItemText from "material-ui/es/List/ListItemText";
import StarIcon from 'material-ui-icons/Star';
import Button from "material-ui/es/Button/Button";
import axios from "axios/index";
import LinearProgress from "material-ui/es/Progress/LinearProgress";
import {getServer} from "../../support/util"

const styles = theme => ({
    root: {
        width: '100%',
    },
    heading: {
        fontSize: theme.typography.pxToRem(15),
        flexBasis: '50%',
        flexShrink: 0,
        color: theme.palette.primary.main
    },
    secondaryHeading: {
        fontSize: theme.typography.pxToRem(15),
        color: theme.palette.text.secondary,
    },
    featureList: {
        width: '100%',
        maxWidth: 360,
        backgroundColor: theme.palette.background.paper,
    },
});

class ExpansionSummary extends React.Component {
    state = {
        expanded: false,
        features: []
    };

    handleChange = () => {
        this.setState({
            expanded: !this.state.expanded,
            features: []
        },()=>{
            if(this.state.expanded){
                this.fetchMilestoneFeatures(this.createIssueObject())
            }
        });
    };
    
    
    generateFeatures(array){
        return array.map((value, index) =>
            <ListItem button key={index}>
                <ListItemIcon>
                    <StarIcon />
                </ListItemIcon>
                <ListItemText inset primary={value} />
            </ListItem>
        );
    }



    // fetch milestone features
    fetchMilestoneFeatures(data) {
        this.setState({
                progressState: true
            }, () => (
                axios.post('http://'+getServer()+'/lts/milestone',
                    data
                ).then(
                    (response) => {
                        let datat = response.data;
                        this.setState({
                            features: this.extractFeatures(datat),
                            progressState: false
                        })
                    }
                )
            )
        );
    }


    extractFeatures(array){
        let featureList = [];
        array.forEach(function (issueFeature) {
            featureList.push(issueFeature["feature"])
        });

        return featureList;
    }

    // create issue url list belong to the milestone
    createIssueObject() {
        let milestoneIssues = [];
        let object = {
            url: this.props.data["url"],
            html_url: this.props.data["html_url"],
            title: this.props.data["issue_title"],
        };

        milestoneIssues.push(object);

        return milestoneIssues;
    }


    render() {
        const { classes } = this.props;
        const { expanded } = this.state;

        return (
                <ExpansionPanel expanded={expanded} >
                    <ExpansionPanelSummary onClick={this.handleChange} expandIcon={<ExpandMoreIcon />}>
                        <Typography className={classes.heading}>{this.props.data["issue_title"]}</Typography>
                        <Typography className={classes.secondaryHeading}>{this.props.data["html_url"]}</Typography>
                    </ExpansionPanelSummary>
                    {this.state.progressState && <LinearProgress color="secondary" />}
                    <ExpansionPanelDetails>
                        <List className={classes.root} dense={true}>
                            {this.generateFeatures(this.state.features)}
                        </List>
                    </ExpansionPanelDetails>
                    <Divider />
                    <Button onClick={() => window.open(this.props.data["html_url"], '_blank')} dense color="secondary">
                        Change Version
                    </Button>
                </ExpansionPanel>

        );
    }
}

ExpansionSummary.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(ExpansionSummary);