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
import AppBar from 'material-ui/AppBar';
import Toolbar from 'material-ui/Toolbar';
import VersionNavigator from './VersionNavigator.js';
import ProductNavigator from './ProductNavigator.js';
import Button from "material-ui/Button";
import QuaterNavigator from "./QuaterNavigator";

const styles = {
    root: {
        width: '100%',
    },
    flex: {
        flex: 1,
    },
    menuButton: {
        marginLeft: -12,
        marginRight: 20,
    },
    header: {
        paddingRight: 20,
        paddingLeft: 25,
        width: `20%`
    },
};

class MenuAppBar extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            version: null,
            versionName:null,
            productId:null,
            productName: null,
            startDate: null,
            endDate: null
        };

        this.setProduct = this.setProduct.bind(this);
        this.setVersion = this.setVersion.bind(this);
        this.openModal = this.openModal.bind(this);
        this.setQuarter = this.setQuarter.bind(this);
    }

    setProduct(productId,productName) {
        this.setState({
                productId: productId,
                productName: productName,
                version: "",
                quarterList:[]
            },
            () => {
                this.props.setprtable(this.state.version,this.state.startDate,this.state.endDate);
            });
    }


    setVersion(versionId,versionName) {
        this.setState({
                version: versionId,
                versionName: versionName
            },
            () => {
                this.props.setNames(this.state.productName,this.state.versionName);
                this.props.setprtable(this.state.version,this.state.startDate,this.state.endDate);
            });
    }


    setQuarter(startDate,endDate){

        this.setState({
            startDate: startDate,
            endDate: endDate,
        },
        ()=>{
            this.props.setprtable(this.state.version,this.state.startDate,this.state.endDate);
        });
    }


    openModal() {
        this.props.featureModal(this.state.product, this.state.version);
    }

    render() {
        const {classes} = this.props;


        return (
            <div className={classes.root}>
                <AppBar position="static" color="default">
                    <Toolbar>
                        <div className={classes.header}>
                            <ProductNavigator
                                setProduct={this.setProduct}
                            />
                        </div>
                        <div className={classes.header}>
                            <VersionNavigator
                                product={this.state.productId}
                                setVersion={this.setVersion}
                            />
                        </div>
                        <div className={classes.header}>
                            <QuaterNavigator
                                setQuarter={this.setQuarter}
                            />
                        </div>
                        <div className={classes.header}>
                            <Button variant="raised" onClick={this.openModal} className={classes.button}>
                                Marketing Messages
                            </Button>
                        </div>

                    </Toolbar>
                </AppBar>
            </div>
        );
    }
}

MenuAppBar.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(MenuAppBar);