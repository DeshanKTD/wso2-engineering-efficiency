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
import ListItem from "material-ui/es/List/ListItem";
import ListItemText from "material-ui/es/List/ListItemText";


class ProductNameItem extends Component{
    constructor(props){
        super(props);
        this.state = {
            productName : this.props.productObject.productName,
            productId: this.props.productObject.productId
        };

        this.getVersions = this.getVersions.bind(this);
    }


    getVersions(){
        this.props.setName(this.state.productName,this.state.productId);
        this.props.getVersionList(this.state.productId);
    }

    render(){
        return(
            <div>
                <ListItem divider={true} disableGutters={true} button onClick={this.getVersions}>
                    <ListItemText inset primary={this.state.productName} />
                </ListItem>
            </div>
        );
    }
}

export default ProductNameItem;