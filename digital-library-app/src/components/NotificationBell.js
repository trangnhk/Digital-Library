import { useContext, useEffect, useState } from "react";
import { MyUserContext } from "../configs/Context";
import { Badge, Dropdown } from "react-bootstrap";
import Apis, { endpoints } from "../configs/Apis";
import MySpinner from "./MySpinner";
import { Bell, BellFill } from "react-bootstrap-icons";
import moment from "moment";
import "moment/locale/vi";

const NotificationBell = () => {
    const [user] = useContext(MyUserContext);

    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(false);
    const [loadingMore, setLoadingMore] = useState(false);
    const [err, setErr] = useState("");


    const size = 10;

    moment.locale("vi");


    const formatTimeAgo = (time) => {
        if (!time) {
            return "Không xác định";
        }

        const parsedTime = moment(
            time,
            [
                "DD-MM-YYYY HH:mm:ss",
                "YYYY-MM-DD HH:mm:ss",
                "YYYY-MM-DD HH:mm:ss.SSSSSS"
            ], true
        );

        if (!parsedTime.isValid()) {
            return time;
        }

        return parsedTime.add(7, "hours").fromNow();
    };

    const loadNotifications = async (pageToLoad = 1, reset = false) => {
        try {
            if (reset) {
                setLoading(true);
            } else {
                setLoadingMore(true);
            }

            setErr("");

            const res = await Apis.get(endpoints.notifications, {
                params: {
                    page: pageToLoad,
                    size: size
                }
            });

            const data = res.data;

            const newItems = data.items || [];

            if (reset) {
                setNotifications(newItems);
            } else {
                setNotifications(prev => {
                    const map = new Map();

                    [...prev, ...newItems].forEach(item => {
                        map.set(item.id, item);
                    });

                    return Array.from(map.values());
                });
            }

            setPage(data.page || pageToLoad);
            setTotalPages(data.totalPages || 1);
            setUnreadCount(data.unreadCount || 0);

        } catch (ex) {
            console.error("LOAD NOTIFICATIONS ERROR:", ex);
            setErr("Không thể tải thông báo.");

        } finally {
            setLoading(false);
            setLoadingMore(false);
        }
    };

    const handleScroll = (e) => {
        const element = e.target;

        const isNearBottom =
            element.scrollTop + element.clientHeight >= element.scrollHeight - 40;

        if (
            isNearBottom &&
            !loading &&
            !loadingMore &&
            page < totalPages
        ) {
            loadNotifications(page + 1, false);
        }
    };

    const markAsRead = async (notification) => {
        if (notification.isRead) {
            return;
        }

        try {
            await Apis.patch(endpoints.readNotification(notification.id));

            setNotifications(prev =>
                prev.map(item =>
                    item.id === notification.id
                        ? { ...item, isRead: true }
                        : item
                )
            );

            setUnreadCount(prev => Math.max(prev - 1, 0));

        } catch (ex) {
            console.error("READ NOTIFICATION ERROR:", ex);
            setErr("Không thể cập nhật trạng thái thông báo.");
        }
    };
    useEffect(() => {
        if (user) {
            loadNotifications(1, true);
        } else {
            setNotifications([]);
            setUnreadCount(0);
        }
    }, [user]);


    if (!user) {
        return null;
    }


    return (
        <Dropdown align="end">
            <Dropdown.Toggle
                variant="light"
                className="position-relative border-0"
                style={{
                    fontSize: "22px",
                    lineHeight: "1"
                }}
            >
                <BellFill color="darkblue" />

                {unreadCount > 0 && (
                    <Badge
                        bg="danger"
                        pill
                        className="position-absolute top-0 start-100 translate-middle"
                        style={{
                            fontSize: "11px"
                        }}
                    >
                        {unreadCount > 99 ? "99+" : unreadCount}
                    </Badge>
                )}
            </Dropdown.Toggle>

            <Dropdown.Menu
                style={{
                    width: "360px",
                    maxHeight: "420px",
                    overflowY: "auto",
                    padding: "0"
                }}
                onScroll={handleScroll}
            >
                <div className="p-3 border-bottom bg-light">
                    <div className="fw-bold">
                        Thông báo
                    </div>

                    <div className="text-muted small">
                        {unreadCount > 0
                            ? `${unreadCount} thông báo chưa đọc`
                            : "Không có thông báo chưa đọc"}
                    </div>
                </div>

                {err && (
                    <div className="p-3 text-danger small">
                        {err}
                    </div>
                )}

                {loading ? (
                    <div className="p-4 text-center">
                        <MySpinner />

                        <div className="text-muted small mt-2">
                            Đang tải thông báo...
                        </div>
                    </div>
                ) : (
                    <>
                        {notifications.length === 0 ? (
                            <div className="p-4 text-center text-muted">
                                Chưa có thông báo.
                            </div>
                        ) : (
                            notifications.map(n => (
                                <Dropdown.Item
                                    key={n.id}
                                    as="div"
                                    onClick={() => markAsRead(n)}
                                    className="border-bottom"
                                    style={{
                                        cursor: "pointer",
                                        whiteSpace: "normal",
                                        backgroundColor: n.isRead ? "#ffffff" : "#f8f9ff"
                                    }}
                                >
                                    <div className="d-flex justify-content-between gap-2">
                                        <div className="fw-semibold">
                                            {n.title}
                                        </div>

                                        {!n.isRead && (
                                            <Badge bg="danger">
                                                New
                                            </Badge>
                                        )}
                                    </div>

                                    <div className="text-muted small mt-1">
                                        {n.message}
                                    </div>

                                    <div className="text-secondary small mt-2">
                                        {formatTimeAgo(n.createdDate)}
                                    </div>
                                </Dropdown.Item>
                            ))
                        )}

                        {loadingMore && (
                            <div className="p-3 text-center">
                                <MySpinner />

                                <div className="text-muted small mt-2">
                                    Đang tải thêm...
                                </div>
                            </div>
                        )}

                        {!loadingMore && page >= totalPages && notifications.length > 0 && (
                            <div className="p-3 text-center text-muted small">
                                Đã hiển thị tất cả thông báo.
                            </div>
                        )}
                    </>
                )}
            </Dropdown.Menu>
        </Dropdown>
    );


};

export default NotificationBell;