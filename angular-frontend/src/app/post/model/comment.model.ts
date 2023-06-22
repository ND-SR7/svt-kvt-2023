export class Comment {
    _id: number;
    text: String;
    timestamp: String;
    repliesToCommentId: number;
    belongsToUserId: number;
    belongsToPostId: number;

    constructor(obj?: any) {
        this._id = obj && obj._id || null;
        this.text = obj && obj.text || null;
        this.timestamp = obj && obj.timestamp || null;
        this.repliesToCommentId = obj && obj.repliesToCommentId || null;
        this.belongsToUserId = obj && obj.belongsToUserId || null;
        this.belongsToPostId = obj && obj.belongsToPostId || null;
    }

    get id() {
        return this._id;
    }
}
