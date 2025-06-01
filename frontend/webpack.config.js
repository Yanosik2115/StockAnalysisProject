const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpack = require('webpack');

module.exports = (env, argv) => {
	const isProduction = argv.mode === 'production';

	return {
		entry: './src/index.tsx',
		output: {
			path: path.resolve(__dirname, 'dist'),
			filename: isProduction ? '[name].[contenthash].js' : '[name].js',
			clean: true,
			publicPath: '/',
		},
		resolve: {
			extensions: ['.tsx', '.ts', '.js', '.jsx'],
			alias: {
				'@': path.resolve(__dirname, 'src'),
				'@components': path.resolve(__dirname, 'src/components'),
				'@store': path.resolve(__dirname, 'src/store'),
				'@types': path.resolve(__dirname, 'src/types'),
				'@utils': path.resolve(__dirname, 'src/utils'),
				'@services': path.resolve(__dirname, 'src/services'),
				'@pages': path.resolve(__dirname, 'src/pages'),
			},
		},
		module: {
			rules: [
				{
					test: /\.tsx?$/,
					use: 'ts-loader',
					exclude: /node_modules/,
				},
				{
					test: /\.css$/i,
					use: ['style-loader', 'css-loader'],
				},
				{
					test: /\.(png|svg|jpg|jpeg|gif)$/i,
					type: 'asset/resource',
				},
				{
					test: /\.(woff|woff2|eot|ttf|otf)$/i,
					type: 'asset/resource',
				},
			],
		},
		plugins: [
			new HtmlWebpackPlugin({
				template: './public/index.html',
				title: 'Stock Analysis App',
				favicon: './public/favicon.ico',
			}),
			new webpack.ProvidePlugin({
				process: 'process/browser.js',
			}),
		],
		devServer: {
			static: {
				directory: path.join(__dirname, 'public'),
			},
			compress: true,
			port: 3000,
			hot: true,
			historyApiFallback: true,
			open: true,
		},
		optimization: {
			splitChunks: {
				chunks: 'all',
				cacheGroups: {
					vendor: {
						test: /[\\/]node_modules[\\/]/,
						name: 'vendors',
						chunks: 'all',
					},
				},
			},
		},
		devtool: isProduction ? 'source-map' : 'eval-source-map',
	};
};