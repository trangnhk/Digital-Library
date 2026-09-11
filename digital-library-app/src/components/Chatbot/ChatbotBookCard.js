const ChatbotBookCard = ({ book }) => {

    return (
        <div className="chatbot-book-card">

            <div className="chatbot-book-thumbnail">
                {book.thumbnail && (<img src={book.thumbnail} alt={book.title}/>)}
            </div>


            <div className="chatbot-book-info">

                <div className="chatbot-book-title">
                    {book.title}
                </div>


                {/* <div className="chatbot-book-id">
                    Book ID: {book.book_id}
                </div> */}

            </div>

        </div>
    );
};

export default ChatbotBookCard;