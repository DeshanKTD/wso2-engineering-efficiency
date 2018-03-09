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

import React, {Component} from 'react';
import logo from './img/WSO2_Software_Logo.png'
import PropTypes from 'prop-types';
import {withStyles} from 'material-ui/styles';
import './App.css';
import AppBar from "material-ui/AppBar";
import Tabs, { Tab } from 'material-ui/Tabs'
import Typography from "material-ui/Typography";
import MarketingWindow from "./maincomponents/MarketingWindow.js";
import ProductVersion from "./maincomponents/ProductVersion";
import ProductRepository from "./maincomponents/ProductRepository";

const styles = theme => ({
    root: {
        flexGrow: 1,
        marginTop: theme.spacing.unit * 3,
        backgroundColor: theme.palette.background.paper,
    },
});


function TabContainer(props) {
    return (
        <Typography component="div" style={{ padding: 8 * 3 }}>
            {props.children}
        </Typography>
    );
}


TabContainer.propTypes = {
    children: PropTypes.node.isRequired,
};




class App extends Component {

    modalOpen = (milestoneData) => {
        this.setState({
            milestoneData: milestoneData,
            openModal: true,
            openFeatureModal: false
        });
    };

    featureModalOpen = (product, version) => {
        this.setState({
            openFeatureModal: true,
            openModal: false,
            product: product,
            version: version
        })
    };

    constructor(props) {
        super(props);
        this.state = {
           value : 0
        };
    }



    handleChange = (event, value) => {
        this.setState({ value });
    };

    render() {
        const { value } = this.state;
        return (
            <div className="App">

                <header className="App-header">
                    <img src={logo} className="App-logo" alt="logo"/>
                    <h1 className="App-title">LTS Dashboard</h1>
                </header>

                <AppBar position="static" color="default">
                    <Tabs value={value} onChange={this.handleChange}>
                        <Tab label="Marketing Messages" />
                        <Tab label="Product Versions" />
                        <Tab label="Repository/ Branches" href="#basic-tabs" />
                    </Tabs>
                </AppBar>
                {value === 0 && <TabContainer><MarketingWindow/></TabContainer>}
                {value === 1 && <TabContainer><ProductVersion/></TabContainer>}
                {value === 2 && <TabContainer><ProductRepository/></TabContainer>}

        </div>

        );
    }
}

App.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(App);
